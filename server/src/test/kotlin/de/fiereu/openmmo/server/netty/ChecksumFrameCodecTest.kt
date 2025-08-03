package de.fiereu.openmmo.server.netty

import de.fiereu.openmmo.server.protocol.tls.checksum.ChecksumFactory
import de.fiereu.openmmo.server.protocol.tls.checksum.Crc16Checksum
import de.fiereu.openmmo.server.protocol.tls.checksum.HmacSha256Checksum
import de.fiereu.openmmo.server.protocol.tls.checksum.NoOpChecksum
import de.fiereu.openmmo.server.netty.handlers.checksum.ChecksumFrameDecoder
import de.fiereu.openmmo.server.netty.handlers.checksum.ChecksumFrameEncoder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel

class ChecksumFrameCodecTest : FunSpec({

    test("NoOpChecksum - encoder should not add checksum") {
        val checksum = NoOpChecksum()
        val encoder = ChecksumFrameEncoder(checksum)
        val channel = EmbeddedChannel(encoder)

        val payload = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)

        channel.writeOutbound(payload)

        val encoded = channel.readAllOutbound()
        encoded.readableBytes() shouldBe 2 // 2 bytes payload (no checksum)
        
        // Check payload
        encoded.readByte() shouldBe 0xAB.toByte()
        encoded.readByte() shouldBe 0xCD.toByte()

        encoded.release()
        channel.close()
    }

    test("NoOpChecksum - decoder should pass through without verification") {
        val checksum = NoOpChecksum()
        val decoder = ChecksumFrameDecoder(checksum)
        val channel = EmbeddedChannel(decoder)

        val input = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)

        channel.writeInbound(input)

        val decoded = channel.readAllInbound()
        decoded.readableBytes() shouldBe 2
        decoded.readByte() shouldBe 0xAB.toByte()
        decoded.readByte() shouldBe 0xCD.toByte()

        decoded.release()
        channel.close()
    }

    test("CRC16 - encoder should append 2-byte checksum") {
        val checksum = Crc16Checksum()
        val encoder = ChecksumFrameEncoder(checksum)
        val channel = EmbeddedChannel(encoder)

        val payload = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)

        channel.writeOutbound(payload)

        val encoded = channel.readAllOutbound()
        encoded.readableBytes() shouldBe 4 // 2 bytes payload + 2 bytes CRC16
        
        // Check payload
        encoded.readByte() shouldBe 0xAB.toByte()
        encoded.readByte() shouldBe 0xCD.toByte()
        
        // Check that checksum bytes are present (we don't verify exact values here)
        encoded.readableBytes() shouldBe 2 // Should have 2 checksum bytes left

        encoded.release()
        channel.close()
    }

    test("CRC16 - decoder should verify and remove checksum") {
        val checksum = Crc16Checksum()
        val decoder = ChecksumFrameDecoder(checksum)
        val channel = EmbeddedChannel(decoder)

        // Create data and calculate its checksum
        val originalData = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)
        
        val checksumBytes = checksum.calculate(originalData)
        
        // Create input with data + checksum
        val input = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)
            .writeBytes(checksumBytes)

        channel.writeInbound(input)

        val decoded = channel.readAllInbound()
        decoded.readableBytes() shouldBe 2 // Checksum should be removed
        decoded.readByte() shouldBe 0xAB.toByte()
        decoded.readByte() shouldBe 0xCD.toByte()

        originalData.release()
        decoded.release()
        channel.close()
    }

    test("CRC16 - decoder should drop packet with invalid checksum") {
        val checksum = Crc16Checksum()
        val decoder = ChecksumFrameDecoder(checksum)
        val channel = EmbeddedChannel(decoder)

        // Create input with data + invalid checksum
        val input = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)
            .writeByte(0xFF) // Invalid checksum
            .writeByte(0xFF)

        channel.writeInbound(input)

        // Should not produce any output due to checksum failure
        channel.readInbound<ByteBuf>() shouldBe null

        channel.close()
    }

    test("HMAC-SHA256 - encoder should append variable-size checksum") {
        val key = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
        val checksum = HmacSha256Checksum(8, key) // 8-byte HMAC
        val encoder = ChecksumFrameEncoder(checksum)
        val channel = EmbeddedChannel(encoder)

        val payload = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)

        channel.writeOutbound(payload)

        val encoded = channel.readAllOutbound()
        encoded.readableBytes() shouldBe 10 // 2 bytes payload + 8 bytes HMAC
        
        // Check payload
        encoded.readByte() shouldBe 0xAB.toByte()
        encoded.readByte() shouldBe 0xCD.toByte()
        
        // Check that HMAC bytes are present
        encoded.readableBytes() shouldBe 8 // Should have 8 HMAC bytes left

        encoded.release()
        channel.close()
    }

    test("HMAC-SHA256 - decoder should verify and remove checksum") {
        val key = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
        val checksum = HmacSha256Checksum(4, key) // 4-byte HMAC
        val decoder = ChecksumFrameDecoder(checksum)
        val channel = EmbeddedChannel(decoder)

        // Create data and calculate its checksum
        val originalData = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)
        
        val checksumBytes = checksum.calculate(originalData)
        
        // Create input with data + checksum
        val input = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)
            .writeBytes(checksumBytes)

        channel.writeInbound(input)

        val decoded = channel.readAllInbound()
        decoded.readableBytes() shouldBe 2 // Checksum should be removed
        decoded.readByte() shouldBe 0xAB.toByte()
        decoded.readByte() shouldBe 0xCD.toByte()

        originalData.release()
        decoded.release()
        channel.close()
    }

    test("HMAC-SHA256 - decoder should drop packet with invalid checksum") {
        val key = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
        val checksum = HmacSha256Checksum(4, key)
        val decoder = ChecksumFrameDecoder(checksum)
        val channel = EmbeddedChannel(decoder)

        // Create input with data + invalid checksum
        val input = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)
            .writeByte(0xFF) // Invalid checksum
            .writeByte(0xFF)
            .writeByte(0xFF)
            .writeByte(0xFF)

        channel.writeInbound(input)

        // Should not produce any output due to checksum failure
        channel.readInbound<ByteBuf>() shouldBe null

        channel.close()
    }

    test("roundtrip - NoOpChecksum encoding and decoding") {
        val checksum = NoOpChecksum()
        val encoder = ChecksumFrameEncoder(checksum)
        val decoder = ChecksumFrameDecoder(checksum)
        val channel = EmbeddedChannel(encoder, decoder)

        val originalPayload = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        val input = Unpooled.buffer().writeBytes(originalPayload)

        // Encode then decode
        channel.writeOutbound(input) shouldBe true
        val encoded = channel.readAllOutbound()
        
        channel.writeInbound(encoded) shouldBe true
        val decoded = channel.readAllInbound()

        decoded.readableBytes() shouldBe originalPayload.size
        for (expectedByte in originalPayload) {
            decoded.readByte() shouldBe expectedByte
        }

        decoded.release()
        channel.close()
    }

    test("roundtrip - CRC16 encoding and decoding") {
        val encoderChecksum = Crc16Checksum()
        val decoderChecksum = Crc16Checksum()
        val encoder = ChecksumFrameEncoder(encoderChecksum)
        val decoder = ChecksumFrameDecoder(decoderChecksum)

        val originalPayload = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        val input = Unpooled.buffer().writeBytes(originalPayload)

        // Encode
        val encoderChannel = EmbeddedChannel(encoder)
        encoderChannel.writeOutbound(input) shouldBe true
        val encoded = encoderChannel.readAllOutbound()

        encoded.readableBytes() shouldBe 6 // 4 bytes payload + 2 bytes CRC16
        
        // Decode
        val decoderChannel = EmbeddedChannel(decoder)
        decoderChannel.writeInbound(encoded) shouldBe true
        val decoded = decoderChannel.readAllInbound()

        decoded.readableBytes() shouldBe originalPayload.size
        for (expectedByte in originalPayload) {
            decoded.readByte() shouldBe expectedByte
        }

        decoded.release()
        encoderChannel.close()
        decoderChannel.close()
    }

    test("roundtrip - HMAC-SHA256 encoding and decoding") {
        val key = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
        val encoderChecksum = HmacSha256Checksum(16, key)
        val decoderChecksum = HmacSha256Checksum(16, key)
        val encoder = ChecksumFrameEncoder(encoderChecksum)
        val decoder = ChecksumFrameDecoder(decoderChecksum)

        val originalPayload = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        val input = Unpooled.buffer().writeBytes(originalPayload)

        // Encode
        val encoderChannel = EmbeddedChannel(encoder)
        encoderChannel.writeOutbound(input) shouldBe true
        val encoded = encoderChannel.readAllOutbound()

        encoded.readableBytes() shouldBe 20 // 4 bytes payload + 16 bytes HMAC
        
        // Decode
        val decoderChannel = EmbeddedChannel(decoder)
        decoderChannel.writeInbound(encoded) shouldBe true
        val decoded = decoderChannel.readAllInbound()

        decoded.readableBytes() shouldBe originalPayload.size
        for (expectedByte in originalPayload) {
            decoded.readByte() shouldBe expectedByte
        }

        decoded.release()
        encoderChannel.close()
        decoderChannel.close()
    }

    test("decoder should handle packet too small for checksum") {
        val checksum = Crc16Checksum()
        val decoder = ChecksumFrameDecoder(checksum)
        val channel = EmbeddedChannel(decoder)

        // Send packet smaller than checksum size
        val input = Unpooled.buffer()
            .writeByte(0xAB) // Only 1 byte, but CRC16 needs 2 bytes

        channel.writeInbound(input)

        // Should not produce any output
        channel.readInbound<ByteBuf>() shouldBe null

        channel.close()
    }

    test("ChecksumFactory - create with size only") {
        val noOpChecksum = ChecksumFactory.create(0)
        (noOpChecksum is NoOpChecksum) shouldBe true
        
        val crc16Checksum = ChecksumFactory.create(2)
        (crc16Checksum is Crc16Checksum) shouldBe true
    }

    test("ChecksumFactory - create with size and key") {
        val key = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        
        val noOpChecksum = ChecksumFactory.create(0, key)
        (noOpChecksum is NoOpChecksum) shouldBe true
        
        val crc16Checksum = ChecksumFactory.create(2, key)
        (crc16Checksum is Crc16Checksum) shouldBe true
        
        val hmacChecksum = ChecksumFactory.create(8, key)
        (hmacChecksum is HmacSha256Checksum) shouldBe true
        hmacChecksum.size shouldBe 8
    }
})
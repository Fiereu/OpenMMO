package de.fiereu.openmmo.server.netty

import de.fiereu.openmmo.server.netty.handlers.frame.PacketFrameDecoder
import de.fiereu.openmmo.server.netty.handlers.frame.PacketFrameEncoder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel

class PacketFrameCodecTest : FunSpec({

    test("decoder should decode frame with correct length prefix") {
        val decoder = PacketFrameDecoder()
        val channel = EmbeddedChannel(decoder)

        // Create input: length 4 (0x0004) + payload [0xAB, 0xCD]
        // Length field includes itself (2 bytes) + payload (2 bytes) = 4 total
        val input = Unpooled.buffer()
            .writeShortLE(4)  // Length field (little endian)
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

    test("decoder should handle empty payload") {
        val decoder = PacketFrameDecoder()
        val channel = EmbeddedChannel(decoder)

        // Create input: length 2 (only the length field itself)
        val input = Unpooled.buffer()
            .writeShortLE(2)  // Length field (little endian)

        channel.writeInbound(input)

        val decoded = channel.readAllInbound()
        decoded.readableBytes() shouldBe 0

        decoded.release()
        channel.close()
    }

    test("decoder should handle larger payload") {
        val decoder = PacketFrameDecoder()
        val channel = EmbeddedChannel(decoder)

        val payload = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)
        val input = Unpooled.buffer()
            .writeShortLE(payload.size + 2)  // Length includes length field
            .writeBytes(payload)

        channel.writeInbound(input)

        val decoded = channel.readAllInbound()
        decoded.readableBytes() shouldBe payload.size
        for (expectedByte in payload) {
            decoded.readByte() shouldBe expectedByte
        }

        decoded.release()
        channel.close()
    }

    test("decoder should not decode incomplete frame") {
        val decoder = PacketFrameDecoder()
        val channel = EmbeddedChannel(decoder)

        // Send only the length field without payload
        val input = Unpooled.buffer()
            .writeShortLE(10)  // Claims 10 bytes total but we only send 2

        channel.writeInbound(input)

        val decoded = channel.readInbound<ByteBuf>()
        decoded.shouldBeNull()

        channel.close()
    }

    test("encoder should prepend correct length field") {
        val encoder = PacketFrameEncoder()
        val channel = EmbeddedChannel(encoder)

        val payload = Unpooled.buffer()
            .writeByte(0xAB)
            .writeByte(0xCD)

        channel.writeOutbound(payload)

        val encoded = channel.readAllOutbound()
        encoded.readableBytes() shouldBe 4
        
        // Check length field (should be 4: 2 bytes length + 2 bytes payload)
        encoded.readUnsignedShortLE() shouldBe 4
        
        // Check payload
        encoded.readByte() shouldBe 0xAB.toByte()
        encoded.readByte() shouldBe 0xCD.toByte()

        encoded.release()
        channel.close()
    }

    test("encoder should handle empty payload") {
        val encoder = PacketFrameEncoder()
        val channel = EmbeddedChannel(encoder)

        val payload = Unpooled.buffer()

        channel.writeOutbound(payload)

        val encoded = channel.readAllOutbound()
        encoded.readableBytes() shouldBe 2
        
        // Check length field (should be 2: only the length field)
        encoded.readUnsignedShortLE() shouldBe 2

        encoded.release()
        channel.close()
    }

    test("encoder should handle larger payload") {
        val encoder = PacketFrameEncoder()
        val channel = EmbeddedChannel(encoder)

        val payloadData = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)
        val payload = Unpooled.buffer().writeBytes(payloadData)

        channel.writeOutbound(payload)

        val encoded = channel.readAllOutbound()
        encoded.readableBytes() shouldBe (payloadData.size + 2)
        
        // Check length field
        encoded.readUnsignedShortLE() shouldBe (payloadData.size + 2)
        
        // Check payload
        for (expectedByte in payloadData) {
            encoded.readByte() shouldBe expectedByte
        }

        encoded.release()
        channel.close()
    }

    test("roundtrip encoding and decoding should work correctly") {
        val encoder = PacketFrameEncoder()
        val decoder = PacketFrameDecoder()
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
})
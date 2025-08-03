package de.fiereu.openmmo.server.netty

import de.fiereu.openmmo.server.protocol.tls.checksum.Crc16Checksum
import de.fiereu.openmmo.server.protocol.tls.checksum.HmacSha256Checksum
import de.fiereu.openmmo.server.protocol.tls.checksum.NoOpChecksum
import de.fiereu.openmmo.server.netty.handlers.checksum.ChecksumFrameDecoder
import de.fiereu.openmmo.server.netty.handlers.checksum.ChecksumFrameEncoder
import de.fiereu.openmmo.server.netty.handlers.frame.PacketFrameDecoder
import de.fiereu.openmmo.server.netty.handlers.frame.PacketFrameEncoder
import de.fiereu.openmmo.server.netty.handlers.tls.TlsDecryptionHandler
import de.fiereu.openmmo.server.netty.handlers.tls.TlsEncryptionHandler
import de.fiereu.openmmo.server.protocol.tls.DefaultTlsContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import io.mockk.every
import io.mockk.mockk
import javax.crypto.Cipher

class PipelineIntegrationTest : FunSpec({

    test("complete outbound pipeline - TLS encryption -> checksum -> frame encoding (NoOpChecksum)") {
        val tlsContext = createMockTlsContext()
        val checksum = NoOpChecksum()
        
        // Create outbound pipeline: TLS encryption -> checksum -> frame encoding
        // Note: In Netty outbound pipeline, handlers are processed in REVERSE order
        val tlsEncoder = TlsEncryptionHandler(tlsContext)
        val checksumEncoder = ChecksumFrameEncoder(checksum)
        val frameEncoder = PacketFrameEncoder()
        
        val outboundChannel = EmbeddedChannel(frameEncoder, checksumEncoder, tlsEncoder)
        
        val originalData = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        val input = Unpooled.buffer().writeBytes(originalData)
        
        outboundChannel.writeOutbound(input) shouldBe true
        val finalOutput = outboundChannel.readAllOutbound()
        
        // Expected structure: [length (2 bytes)] + [encrypted_data (4 bytes)] + [checksum (0 bytes for NoOp)]
        finalOutput.readableBytes() shouldBe 6 // 2 bytes length + 4 bytes encrypted data + 0 bytes checksum
        
        // Read and verify length field (little endian)
        val lengthField = finalOutput.readUnsignedShortLE()
        lengthField shouldBe 6 // Length includes itself (2) + encrypted data (4) + checksum (0)
        
        // Read the encrypted data
        val encryptedData = ByteArray(4)
        finalOutput.readBytes(encryptedData)
        
        // Encrypted data should be different from original (XOR with 0x42)
        encryptedData shouldNotBe originalData
        // Verify it's actually XOR encrypted
        val expectedEncrypted = originalData.map { (it.toInt() xor 0x42).toByte() }.toByteArray()
        encryptedData shouldBe expectedEncrypted
        
        finalOutput.release()
        outboundChannel.close()
    }

    test("complete outbound pipeline - TLS encryption -> checksum -> frame encoding (CRC16)") {
        val tlsContext = createMockTlsContext()
        val checksum = Crc16Checksum()
        
        val tlsEncoder = TlsEncryptionHandler(tlsContext)
        val checksumEncoder = ChecksumFrameEncoder(checksum)
        val frameEncoder = PacketFrameEncoder()
        
        val outboundChannel = EmbeddedChannel(frameEncoder, checksumEncoder, tlsEncoder)
        
        val originalData = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        val input = Unpooled.buffer().writeBytes(originalData)
        
        outboundChannel.writeOutbound(input) shouldBe true
        val finalOutput = outboundChannel.readAllOutbound()
        
        // Expected structure: [length (2 bytes)] + [encrypted_data (4 bytes)] + [crc16 checksum (2 bytes)]
        finalOutput.readableBytes() shouldBe 8 // 2 + 4 + 2
        
        val lengthField = finalOutput.readUnsignedShortLE()
        lengthField shouldBe 8
        
        // Read the encrypted data
        val encryptedData = ByteArray(4)
        finalOutput.readBytes(encryptedData)
        encryptedData shouldNotBe originalData
        
        // Read the CRC16 checksum
        val checksumData = ByteArray(2)
        finalOutput.readBytes(checksumData)
        // Verify checksum exists (non-zero for this data pattern)
        checksumData shouldNotBe byteArrayOf(0, 0)
        
        finalOutput.release()
        outboundChannel.close()
    }

    test("complete outbound pipeline - TLS encryption -> checksum -> frame encoding (HMAC-SHA256)") {
        val tlsContext = createMockTlsContext()
        val key = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
        val checksum = HmacSha256Checksum(8, key)
        
        val tlsEncoder = TlsEncryptionHandler(tlsContext)
        val checksumEncoder = ChecksumFrameEncoder(checksum)
        val frameEncoder = PacketFrameEncoder()
        
        val outboundChannel = EmbeddedChannel(frameEncoder, checksumEncoder, tlsEncoder)
        
        val originalData = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        val input = Unpooled.buffer().writeBytes(originalData)
        
        outboundChannel.writeOutbound(input) shouldBe true
        val finalOutput = outboundChannel.readAllOutbound()
        
        // Expected structure: [length (2 bytes)] + [encrypted_data (4 bytes)] + [hmac (8 bytes)]
        finalOutput.readableBytes() shouldBe 14 // 2 + 4 + 8
        
        val lengthField = finalOutput.readUnsignedShortLE()
        lengthField shouldBe 14
        
        // Read the encrypted data
        val encryptedData = ByteArray(4)
        finalOutput.readBytes(encryptedData)
        encryptedData shouldNotBe originalData
        
        // Read the HMAC
        val hmacData = ByteArray(8)
        finalOutput.readBytes(hmacData)
        // HMAC should exist and be non-zero
        hmacData shouldNotBe ByteArray(8) { 0 }
        
        finalOutput.release()
        outboundChannel.close()
    }

    test("complete inbound pipeline - frame decoding -> checksum verification -> TLS decryption (NoOpChecksum)") {
        val tlsContext = createMockTlsContext()
        val checksum = NoOpChecksum()
        
        // Create inbound pipeline: frame decoding -> checksum verification -> TLS decryption
        val frameDecoder = PacketFrameDecoder()
        val checksumDecoder = ChecksumFrameDecoder(checksum)
        val tlsDecoder = TlsDecryptionHandler(tlsContext)
        
        val inboundChannel = EmbeddedChannel(frameDecoder, checksumDecoder, tlsDecoder)
        
        val originalData = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        
        // First encrypt the data to simulate what would come from the outbound pipeline
        val encryptedData = originalData.map { (it.toInt() xor 0x42).toByte() }.toByteArray()
        
        // Create the complete packet: length + encrypted data (no checksum for NoOp)
        val packetLength = 2 + encryptedData.size // length field + data
        val completePacket = Unpooled.buffer()
            .writeShortLE(packetLength)
            .writeBytes(encryptedData)
        
        inboundChannel.writeInbound(completePacket) shouldBe true
        val decryptedOutput = inboundChannel.readAllInbound()
        
        decryptedOutput.readableBytes() shouldBe originalData.size
        val decryptedBytes = ByteArray(decryptedOutput.readableBytes())
        decryptedOutput.readBytes(decryptedBytes)
        
        // Should get back the original data
        decryptedBytes shouldBe originalData
        
        decryptedOutput.release()
        inboundChannel.close()
    }

    test("complete inbound pipeline - frame decoding -> checksum verification -> TLS decryption (CRC16)") {
        val tlsContext = createMockTlsContext()
        val checksum = Crc16Checksum()
        
        val frameDecoder = PacketFrameDecoder()
        val checksumDecoder = ChecksumFrameDecoder(checksum)
        val tlsDecoder = TlsDecryptionHandler(tlsContext)
        
        val inboundChannel = EmbeddedChannel(frameDecoder, checksumDecoder, tlsDecoder)
        
        val originalData = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        
        // Encrypt the data
        val encryptedData = originalData.map { (it.toInt() xor 0x42).toByte() }.toByteArray()
        
        // Calculate checksum for the encrypted data
        val encryptedBuffer = Unpooled.buffer().writeBytes(encryptedData)
        val checksumBytes = checksum.calculate(encryptedBuffer)
        encryptedBuffer.release()
        
        // Create the complete packet: length + encrypted data + checksum
        val packetLength = 2 + encryptedData.size + checksumBytes.size
        val completePacket = Unpooled.buffer()
            .writeShortLE(packetLength)
            .writeBytes(encryptedData)
            .writeBytes(checksumBytes)
        
        inboundChannel.writeInbound(completePacket) shouldBe true
        val decryptedOutput = inboundChannel.readAllInbound()
        
        decryptedOutput.readableBytes() shouldBe originalData.size
        val decryptedBytes = ByteArray(decryptedOutput.readableBytes())
        decryptedOutput.readBytes(decryptedBytes)
        
        decryptedBytes shouldBe originalData
        
        decryptedOutput.release()
        inboundChannel.close()
    }

    test("complete inbound pipeline - frame decoding -> checksum verification -> TLS decryption (HMAC-SHA256)") {
        val tlsContext = createMockTlsContext()
        val key = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
        val checksum = HmacSha256Checksum(8, key)
        
        val frameDecoder = PacketFrameDecoder()
        val checksumDecoder = ChecksumFrameDecoder(checksum)
        val tlsDecoder = TlsDecryptionHandler(tlsContext)
        
        val inboundChannel = EmbeddedChannel(frameDecoder, checksumDecoder, tlsDecoder)
        
        val originalData = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        
        // Encrypt the data
        val encryptedData = originalData.map { (it.toInt() xor 0x42).toByte() }.toByteArray()
        
        // Calculate HMAC for the encrypted data
        val encryptedBuffer = Unpooled.buffer().writeBytes(encryptedData)
        val hmacBytes = checksum.calculate(encryptedBuffer)
        encryptedBuffer.release()
        
        // Create the complete packet: length + encrypted data + hmac
        val packetLength = 2 + encryptedData.size + hmacBytes.size
        val completePacket = Unpooled.buffer()
            .writeShortLE(packetLength)
            .writeBytes(encryptedData)
            .writeBytes(hmacBytes)
        
        inboundChannel.writeInbound(completePacket) shouldBe true
        val decryptedOutput = inboundChannel.readAllInbound()
        
        decryptedOutput.readableBytes() shouldBe originalData.size
        val decryptedBytes = ByteArray(decryptedOutput.readableBytes())
        decryptedOutput.readBytes(decryptedBytes)
        
        decryptedBytes shouldBe originalData
        
        decryptedOutput.release()
        inboundChannel.close()
    }

    test("complete roundtrip - outbound then inbound pipeline should preserve original data") {
        val tlsContext = createMockTlsContext()
        val checksum = Crc16Checksum()
        
        // Outbound pipeline: TLS encryption -> checksum -> frame encoding
        val tlsEncoder = TlsEncryptionHandler(tlsContext)
        val checksumEncoder = ChecksumFrameEncoder(checksum)
        val frameEncoder = PacketFrameEncoder()
        val outboundChannel = EmbeddedChannel(frameEncoder, checksumEncoder, tlsEncoder)
        
        // Inbound pipeline: frame decoding -> checksum verification -> TLS decryption
        val frameDecoder = PacketFrameDecoder()
        val checksumDecoder = ChecksumFrameDecoder(checksum)
        val tlsDecoder = TlsDecryptionHandler(tlsContext)
        val inboundChannel = EmbeddedChannel(frameDecoder, checksumDecoder, tlsDecoder)
        
        val originalData = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        val input = Unpooled.buffer().writeBytes(originalData)
        
        // Process through outbound pipeline
        outboundChannel.writeOutbound(input) shouldBe true
        val processedPacket = outboundChannel.readAllOutbound()
        
        // Process through inbound pipeline
        inboundChannel.writeInbound(processedPacket) shouldBe true
        val finalOutput = inboundChannel.readAllInbound()
        
        // Should get back the original data
        finalOutput.readableBytes() shouldBe originalData.size
        val finalBytes = ByteArray(finalOutput.readableBytes())
        finalOutput.readBytes(finalBytes)
        finalBytes shouldBe originalData
        
        finalOutput.release()
        outboundChannel.close()
        inboundChannel.close()
    }

    test("complete roundtrip with multiple packets should maintain order and content") {
        // Create separate TLS contexts for encoder and decoder to avoid stream cipher sync issues
        val encoderTlsContext = createMockTlsContext()
        val decoderTlsContext = createMockTlsContext() // Same XOR key, but separate cipher state
        val key = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
        
        // Create separate checksum instances for encoder and decoder
        // Both start with the same key but maintain separate round counters
        val encoderChecksum = HmacSha256Checksum(4, key)
        val decoderChecksum = HmacSha256Checksum(4, key)
        
        // Create pipelines
        val tlsEncoder = TlsEncryptionHandler(encoderTlsContext)
        val checksumEncoder = ChecksumFrameEncoder(encoderChecksum)
        val frameEncoder = PacketFrameEncoder()
        val outboundChannel = EmbeddedChannel(frameEncoder, checksumEncoder, tlsEncoder)
        
        val frameDecoder = PacketFrameDecoder()
        val checksumDecoder = ChecksumFrameDecoder(decoderChecksum)
        val tlsDecoder = TlsDecryptionHandler(decoderTlsContext)
        val inboundChannel = EmbeddedChannel(frameDecoder, checksumDecoder, tlsDecoder)
        
        val packets = listOf(
            byteArrayOf(0x01, 0x02, 0x03),
            byteArrayOf(0x04, 0x05, 0x06, 0x07),
            byteArrayOf(0x08, 0x09),
            byteArrayOf(0x0A, 0x0B, 0x0C, 0x0D, 0x0E)
        )
        
        val processedPackets = mutableListOf<ByteArray>()
        
        // Process each packet through the complete pipeline
        for (originalPacket in packets) {
            val input = Unpooled.buffer().writeBytes(originalPacket)
            
            // Outbound processing
            outboundChannel.writeOutbound(input) shouldBe true
            val processedPacket = outboundChannel.readAllOutbound()
            
            // Inbound processing
            inboundChannel.writeInbound(processedPacket) shouldBe true
            val finalOutput = inboundChannel.readAllInbound()
            
            val finalBytes = ByteArray(finalOutput.readableBytes())
            finalOutput.readBytes(finalBytes)
            processedPackets.add(finalBytes)
            
            finalOutput.release()
        }
        
        // Verify all packets were processed correctly
        packets.forEachIndexed { index, originalPacket ->
            processedPackets[index] shouldBe originalPacket
        }
        
        outboundChannel.close()
        inboundChannel.close()
    }

    test("inbound pipeline should reject packet with invalid checksum") {
        val tlsContext = createMockTlsContext()
        val checksum = Crc16Checksum()
        
        val frameDecoder = PacketFrameDecoder()
        val checksumDecoder = ChecksumFrameDecoder(checksum)
        val tlsDecoder = TlsDecryptionHandler(tlsContext)
        val inboundChannel = EmbeddedChannel(frameDecoder, checksumDecoder, tlsDecoder)
        
        val originalData = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        val encryptedData = originalData.map { (it.toInt() xor 0x42).toByte() }.toByteArray()
        
        // Create packet with invalid checksum
        val packetLength = 2 + encryptedData.size + 2 // length + data + invalid checksum
        val invalidPacket = Unpooled.buffer()
            .writeShortLE(packetLength)
            .writeBytes(encryptedData)
            .writeByte(0xFF) // Invalid checksum
            .writeByte(0xFF)
        
        inboundChannel.writeInbound(invalidPacket) shouldBe false
        
        // Should not produce any output due to checksum failure
        inboundChannel.readInbound<ByteBuf>() shouldBe null
        
        inboundChannel.close()
    }
})
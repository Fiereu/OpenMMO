package de.fiereu.openmmo.server.netty

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

class TlsHandlerTest : FunSpec({

    test("TlsEncryptionHandler should encrypt data") {
        val tlsContext = createMockTlsContext()
        val encoder = TlsEncryptionHandler(tlsContext)
        val channel = EmbeddedChannel(encoder)

        val originalData = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        val input = Unpooled.buffer().writeBytes(originalData)

        channel.writeOutbound(input)

        val encrypted = channel.readAllOutbound()
        encrypted.readableBytes() shouldBe originalData.size

        // The encrypted data should be different from the original
        val encryptedBytes = ByteArray(encrypted.readableBytes())
        encrypted.readBytes(encryptedBytes)
        encryptedBytes shouldNotBe originalData

        encrypted.release()
        channel.close()
    }

    test("TlsDecryptionHandler should decrypt data") {
        val tlsContext = createMockTlsContext()
        val decoder = TlsDecryptionHandler(tlsContext)
        val channel = EmbeddedChannel(decoder)

        val originalData = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        
        // First encrypt the data using the TLS context directly
        val encryptedBytes = tlsContext.encrypt(originalData)
        val input = Unpooled.buffer().writeBytes(encryptedBytes)

        channel.writeInbound(input)

        val decrypted = channel.readAllInbound()
        decrypted.readableBytes() shouldBe originalData.size

        val decryptedBytes = ByteArray(decrypted.readableBytes())
        decrypted.readBytes(decryptedBytes)
        decryptedBytes shouldBe originalData

        decrypted.release()
        channel.close()
    }

    test("TlsEncryptionHandler should handle empty data") {
        val tlsContext = createMockTlsContext()
        val encoder = TlsEncryptionHandler(tlsContext)
        val channel = EmbeddedChannel(encoder)

        val input = Unpooled.buffer() // Empty buffer

        channel.writeOutbound(input)

        val encrypted = channel.readAllOutbound()
        encrypted.readableBytes() shouldBe 0

        encrypted.release()
        channel.close()
    }

    test("TlsDecryptionHandler should handle empty data") {
        val tlsContext = createMockTlsContext()
        val decoder = TlsDecryptionHandler(tlsContext)
        val channel = EmbeddedChannel(decoder)

        val input = Unpooled.buffer() // Empty buffer

        channel.writeInbound(input)

        // Should not produce any output for empty input
        channel.readInbound<ByteBuf>() shouldBe null

        channel.close()
    }

    test("TlsEncryptionHandler should handle large data") {
        val tlsContext = createMockTlsContext()
        val encoder = TlsEncryptionHandler(tlsContext)
        val channel = EmbeddedChannel(encoder)

        val largeData = ByteArray(1024) { it.toByte() }
        val input = Unpooled.buffer().writeBytes(largeData)

        channel.writeOutbound(input)

        val encrypted = channel.readAllOutbound()
        encrypted.readableBytes() shouldBe largeData.size

        // The encrypted data should be different from the original
        val encryptedBytes = ByteArray(encrypted.readableBytes())
        encrypted.readBytes(encryptedBytes)
        encryptedBytes shouldNotBe largeData

        encrypted.release()
        channel.close()
    }

    test("TlsDecryptionHandler should handle large data") {
        val tlsContext = createMockTlsContext()
        val decoder = TlsDecryptionHandler(tlsContext)
        val channel = EmbeddedChannel(decoder)

        val largeData = ByteArray(1024) { it.toByte() }
        
        // First encrypt the data using the TLS context directly
        val encryptedBytes = tlsContext.encrypt(largeData)
        val input = Unpooled.buffer().writeBytes(encryptedBytes)

        channel.writeInbound(input)

        val decrypted = channel.readAllInbound()
        decrypted.readableBytes() shouldBe largeData.size

        val decryptedBytes = ByteArray(decrypted.readableBytes())
        decrypted.readBytes(decryptedBytes)
        decryptedBytes shouldBe largeData

        decrypted.release()
        channel.close()
    }

    test("roundtrip - encryption then decryption should preserve data") {
        val tlsContext = createMockTlsContext()
        val encoder = TlsEncryptionHandler(tlsContext)
        val decoder = TlsDecryptionHandler(tlsContext)

        val originalData = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        val input = Unpooled.buffer().writeBytes(originalData)

        // Encrypt
        val encryptChannel = EmbeddedChannel(encoder)
        encryptChannel.writeOutbound(input) shouldBe true
        val encrypted = encryptChannel.readAllOutbound()

        // Decrypt
        val decryptChannel = EmbeddedChannel(decoder)
        decryptChannel.writeInbound(encrypted) shouldBe true
        val decrypted = decryptChannel.readAllInbound()

        decrypted.readableBytes() shouldBe originalData.size
        val decryptedBytes = ByteArray(decrypted.readableBytes())
        decrypted.readBytes(decryptedBytes)
        decryptedBytes shouldBe originalData

        decrypted.release()
        encryptChannel.close()
        decryptChannel.close()
    }

    test("roundtrip - multiple packets should maintain stream cipher state") {
        val tlsContext = createMockTlsContext()
        val encoder = TlsEncryptionHandler(tlsContext)
        val decoder = TlsDecryptionHandler(tlsContext)

        val encryptChannel = EmbeddedChannel(encoder)
        val decryptChannel = EmbeddedChannel(decoder)

        val packets = listOf(
            byteArrayOf(0x01, 0x02, 0x03),
            byteArrayOf(0x04, 0x05),
            byteArrayOf(0x06, 0x07, 0x08, 0x09)
        )

        val decryptedPackets = mutableListOf<ByteArray>()

        // Process each packet in order (maintaining stream cipher state)
        for (packet in packets) {
            val input = Unpooled.buffer().writeBytes(packet)
            
            // Encrypt
            encryptChannel.writeOutbound(input) shouldBe true
            val encrypted = encryptChannel.readAllOutbound()
            
            // Decrypt
            decryptChannel.writeInbound(encrypted) shouldBe true
            val decrypted = decryptChannel.readAllInbound()
            
            val decryptedBytes = ByteArray(decrypted.readableBytes())
            decrypted.readBytes(decryptedBytes)
            decryptedPackets.add(decryptedBytes)
            
            decrypted.release()
        }

        // Verify all packets were decrypted correctly
        packets.forEachIndexed { index, originalPacket ->
            decryptedPackets[index] shouldBe originalPacket
        }

        encryptChannel.close()
        decryptChannel.close()
    }

    test("two different TlsContexts should produce different ciphertexts") {
        val tlsContext1 = createMockTlsContext()
        val tlsContext2 = createMockTlsContext(0x24)
        
        val encoder1 = TlsEncryptionHandler(tlsContext1)
        val encoder2 = TlsEncryptionHandler(tlsContext2)
        
        val channel1 = EmbeddedChannel(encoder1)
        val channel2 = EmbeddedChannel(encoder2)

        val originalData = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte(), 0x12.toByte())
        val input1 = Unpooled.buffer().writeBytes(originalData)
        val input2 = Unpooled.buffer().writeBytes(originalData)

        channel1.writeOutbound(input1)
        channel2.writeOutbound(input2)

        val encrypted1 = channel1.readAllOutbound()
        val encrypted2 = channel2.readAllOutbound()

        encrypted1.readableBytes() shouldBe encrypted2.readableBytes()

        val encrypted1Bytes = ByteArray(encrypted1.readableBytes())
        val encrypted2Bytes = ByteArray(encrypted2.readableBytes())
        encrypted1.readBytes(encrypted1Bytes)
        encrypted2.readBytes(encrypted2Bytes)

        // Different contexts should produce different ciphertext
        encrypted1Bytes shouldNotBe encrypted2Bytes

        encrypted1.release()
        encrypted2.release()
        channel1.close()
        channel2.close()
    }
})
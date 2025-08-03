package de.fiereu.openmmo.server.protocol.tls.checksum

import io.netty.buffer.ByteBuf
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class HmacSha256Checksum(
    override val size: Int,
    key: ByteArray
) : Checksum {
    
    private val mac: Mac
    private var hashRound = 0
    private var verifyRound = 0

    init {
        require(size in 4..32) { "HMAC-SHA256 size must be between 4 and 32 bytes" }
        
        mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
    }

    override fun calculate(data: ByteBuf): ByteArray {
        // Read data without modifying the buffer's reader index
        val dataBytes = ByteArray(data.readableBytes())
        data.getBytes(data.readerIndex(), dataBytes)
        
        mac.update(dataBytes)
        mac.update(getBytes(hashRound++))
        val result = mac.doFinal()
        return result.copyOfRange(0, size)
    }

    override fun verify(data: ByteBuf, expectedChecksum: ByteArray): Boolean {
        if (expectedChecksum.size != size) return false
        
        // Read data without modifying the buffer's reader index  
        val dataBytes = ByteArray(data.readableBytes())
        data.getBytes(data.readerIndex(), dataBytes)
        
        mac.update(dataBytes)
        mac.update(getBytes(verifyRound++))
        val result = mac.doFinal()
        val actualChecksum = result.copyOfRange(0, size)
        
        return MessageDigest.isEqual(actualChecksum, expectedChecksum)
    }

    /**
     * Convert an integer to a big endian byte array.
     * @param value The integer to convert.
     * @return The big endian byte array.
     */
    private fun getBytes(value: Int): ByteArray {
        return byteArrayOf(
            ((value shr 24) and 0xFF).toByte(),
            ((value shr 16) and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
            (value and 0xFF).toByte()
        )
    }
}
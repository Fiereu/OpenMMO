package de.fiereu.openmmo.server.protocol.tls.checksum

import io.netty.buffer.ByteBuf

interface Checksum {
    val size: Int
    
    fun calculate(data: ByteBuf): ByteArray
    fun verify(data: ByteBuf, expectedChecksum: ByteArray): Boolean
}

class NoOpChecksum : Checksum {
    override val size: Int = 0
    
    override fun calculate(data: ByteBuf): ByteArray = ByteArray(0)
    
    override fun verify(data: ByteBuf, expectedChecksum: ByteArray): Boolean = true
}

object ChecksumFactory {
    fun create(checksumSize: Int): Checksum {
        return create(checksumSize, ByteArray(0))
    }
    
    fun create(checksumSize: Int, key: ByteArray): Checksum {
        return when (checksumSize) {
            0 -> NoOpChecksum()
            2 -> Crc16Checksum()
            in 4..32 -> HmacSha256Checksum(checksumSize, key)
            else -> throw IllegalArgumentException("Unsupported checksum size: $checksumSize")
        }
    }
}
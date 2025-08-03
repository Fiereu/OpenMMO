package de.fiereu.openmmo.server.netty.handlers.checksum

import de.fiereu.openmmo.server.protocol.tls.checksum.Checksum
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class ChecksumFrameEncoder(
    private val checksum: Checksum
) : MessageToByteEncoder<ByteBuf>() {

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        val dataLength = msg.readableBytes()
        
        // Calculate checksum on the original data (before adding length)
        val checksumBytes = if (checksum.size > 0) {
            checksum.calculate(msg)
        } else {
            ByteArray(0)
        }

        // Write the packet data
        out.writeBytes(msg, msg.readerIndex(), dataLength)
        
        // Append checksum if needed
        if (checksumBytes.isNotEmpty()) {
            out.writeBytes(checksumBytes)
        }
    }
}
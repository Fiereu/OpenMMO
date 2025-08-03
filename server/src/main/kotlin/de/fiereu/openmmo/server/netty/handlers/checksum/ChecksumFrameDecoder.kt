package de.fiereu.openmmo.server.netty.handlers.checksum

import de.fiereu.openmmo.server.protocol.tls.checksum.Checksum
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

private val log = KotlinLogging.logger {}

class ChecksumFrameDecoder(
    private val checksum: Checksum
) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        // The regular FrameDecoder already handled length field extraction
        // We receive the complete packet data here
        
        if (checksum.size > 0) {
            if (buffer.readableBytes() < checksum.size) {
                log.warn { "Packet too small for checksum verification" }
                return
            }

            // Extract checksum from end of packet
            val checksumBytes = ByteArray(checksum.size)
            buffer.getBytes(buffer.readableBytes() - checksum.size, checksumBytes)

            // Verify checksum (excluding the checksum bytes themselves)
            val dataToVerify = buffer.slice(0, buffer.readableBytes() - checksum.size)
            if (!checksum.verify(dataToVerify, checksumBytes)) {
                log.warn { "Checksum verification failed, dropping packet" }
                return
            }

            // Remove checksum from packet data and pass it on
            // Use slice() instead of readSlice() to avoid modifying the buffer's reader index
            val packetWithoutChecksum = buffer.slice(buffer.readerIndex(), buffer.readableBytes() - checksum.size)
            out.add(packetWithoutChecksum.retain())
            // Advance the reader index to mark the data as consumed
            buffer.readerIndex(buffer.writerIndex())
        } else { // We could use the NoOpChecksum here but this is faster
            // No checksum, pass through as-is
            out.add(buffer.slice(buffer.readerIndex(), buffer.readableBytes()).retain())
            // Advance the reader index to mark the data as consumed
            buffer.readerIndex(buffer.writerIndex())
        }
    }
}
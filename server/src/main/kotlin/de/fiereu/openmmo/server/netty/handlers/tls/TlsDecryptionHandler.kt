package de.fiereu.openmmo.server.netty.handlers.tls

import de.fiereu.openmmo.server.protocol.tls.TlsContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class TlsDecryptionHandler(
    private val tlsContext: TlsContext
) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        val dataLength = buffer.readableBytes()
        if (dataLength == 0) return
        
        val encryptedBytes = ByteArray(dataLength)
        buffer.readBytes(encryptedBytes)

        val decryptedBytes = tlsContext.decrypt(encryptedBytes)
        
        val decryptedBuffer = ctx.alloc().buffer(decryptedBytes.size)
        decryptedBuffer.writeBytes(decryptedBytes)
        out.add(decryptedBuffer)
    }
}
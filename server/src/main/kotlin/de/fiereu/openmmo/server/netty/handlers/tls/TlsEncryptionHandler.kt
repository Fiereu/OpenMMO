package de.fiereu.openmmo.server.netty.handlers.tls

import de.fiereu.openmmo.server.protocol.tls.TlsContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class TlsEncryptionHandler(
  private val tlsContext: TlsContext
) : MessageToByteEncoder<ByteBuf>() {

  override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
    val dataLength = msg.readableBytes()
    val inputBytes = ByteArray(dataLength)
    msg.readBytes(inputBytes)

    val encryptedBytes = tlsContext.encrypt(inputBytes)

    out.writeBytes(encryptedBytes)
  }
}
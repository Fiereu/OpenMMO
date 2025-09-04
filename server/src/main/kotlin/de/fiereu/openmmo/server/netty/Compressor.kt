package de.fiereu.openmmo.server.netty

import de.fiereu.openmmo.server.config.ServerConfig
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import java.util.zip.Deflater

class Compressor(
  serverConfig: ServerConfig,
) {
  private val deflater = Deflater(Deflater.DEFAULT_COMPRESSION, true)
  /**
   * At which packet size we need to compress the packet.
   */
  private val threshold: Int = (serverConfig.sendBufferSize * 0.9).toInt()

  fun compress(
    msg: ByteBuf
  ) : ByteBuf {
    val dataLength = msg.readableBytes()
    if (dataLength < threshold) {
      val outputBuffer = Unpooled
        .buffer(dataLength + 1)
        .writeBoolean(false)
        .writeBytes(msg)

      return outputBuffer
    }

    // Maybe use pre-allocated buffer?
    val inputBuffer = Unpooled.copiedBuffer(msg)
      .writeShortLE(0xFFFF)

    deflater.setInput(
      inputBuffer.array(),
      inputBuffer.arrayOffset() + inputBuffer.readerIndex(),
      inputBuffer.readableBytes()
    )
    val outputBuffer = Unpooled
      .buffer(dataLength + 1)
      .writeBoolean(true)

    val buffer = ByteArray(Short.MAX_VALUE.toInt())
    do {
      val read = deflater.deflate(buffer)
      if (read == 0) break

      outputBuffer.writeBytes(buffer, 0, read)
    } while (true)

    return outputBuffer
  }
}
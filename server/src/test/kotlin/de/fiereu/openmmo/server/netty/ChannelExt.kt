package de.fiereu.openmmo.server.netty

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel

fun EmbeddedChannel.readAllOutbound(): ByteBuf {
  val encoded = Unpooled.buffer()
  var buf: ByteBuf?
  while (true) {
    buf = this.readOutbound<ByteBuf>()
    if (buf == null) break
    encoded.writeBytes(buf)
    buf.release()
  }
  return encoded
}

fun EmbeddedChannel.readAllInbound(): ByteBuf {
  val encoded = Unpooled.buffer()
  var buf: ByteBuf?
  while (true) {
    buf = this.readInbound<ByteBuf>()
    if (buf == null) break
    encoded.writeBytes(buf)
    buf.release()
  }
  return encoded
}
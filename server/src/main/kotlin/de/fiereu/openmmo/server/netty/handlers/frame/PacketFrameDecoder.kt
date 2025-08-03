package de.fiereu.openmmo.server.netty.handlers.frame

import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import java.nio.ByteOrder

class PacketFrameDecoder : LengthFieldBasedFrameDecoder(
  ByteOrder.LITTLE_ENDIAN,
  UShort.MAX_VALUE.toInt(),
  0,
  UShort.SIZE_BYTES,
  -UShort.SIZE_BYTES, // Length includes the length field itself, so subtract 2
  UShort.SIZE_BYTES,
  false
)
package de.fiereu.openmmo.server.netty.handlers.frame

import io.netty.handler.codec.LengthFieldPrepender
import java.nio.ByteOrder

class PacketFrameEncoder : LengthFieldPrepender(
    ByteOrder.LITTLE_ENDIAN,
    UShort.SIZE_BYTES,
    0,
    true
)
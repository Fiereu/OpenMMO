package de.fiereu.openmmo.protocols.game.packets.codecs

import de.fiereu.openmmo.common.Tile2D
import io.netty.buffer.ByteBuf

fun ByteBuf.readTile2DLE(): Tile2D {
  val raw = readShortLE().toInt()
  return Tile2D(material = (raw and 0x3FF).toShort(), collision = ((raw shr 10) and 0x3F).toByte())
}

fun ByteBuf.writeTile2DLE(tile: Tile2D) {
  val raw = ((tile.collision.toInt() and 0x3F) shl 10) or (tile.material.toInt() and 0x3FF)
  writeShortLE(raw)
}

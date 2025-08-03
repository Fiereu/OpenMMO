package de.fiereu.openmmo.protocols

import io.netty.buffer.ByteBuf

fun ByteBuf.readCharLE(): Char {
  return ((readUnsignedByte().toInt() or (readUnsignedByte().toInt() shl 8)) and 0xFFFF).toChar()
}

fun ByteBuf.writeCharLE(value: Char) {
  writeByte(value.code and 0xFF)
  writeByte((value.code shr 8) and 0xFF)
}

fun ByteBuf.readUtf16LE(): String {
  val builder = StringBuilder()
  var c: Char
  while ((readCharLE().also { c = it }).code != 0) {
    builder.append(c)
  }
  return builder.toString()
}

fun ByteBuf.writeUtf16LE(value: String) {
  for (c in value) {
    writeCharLE(c)
  }
  writeCharLE(0.toChar()) // Null-terminator
}
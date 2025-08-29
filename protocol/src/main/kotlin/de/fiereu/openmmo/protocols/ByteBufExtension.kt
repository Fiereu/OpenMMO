package de.fiereu.openmmo.protocols

import com.github.maltalex.ineter.base.IPAddress
import com.github.maltalex.ineter.base.IPv4Address
import com.github.maltalex.ineter.base.IPv6Address
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

fun ByteBuf.readIpLE(): IPAddress {
  val type = readUnsignedByte().toInt()
  return when (type) {
    4 -> {
      val intValue = readIntLE()
      IPv4Address.of(intValue)
    }
    6 -> {
      val upper = readLongLE()
      val lower = readLongLE()
      IPv6Address.of(upper, lower)
    }
    else -> throw IllegalArgumentException("Unknown IP address type: $type")
  }
}

fun ByteBuf.writeIpLE(ipAddress: IPAddress) {
  when (ipAddress) {
    is IPv4Address -> {
      writeByte(4)
      writeIntLE(ipAddress.toInt())
    }

    is IPv6Address -> {
      writeByte(6)
      writeLongLE(ipAddress.upper)
      writeLongLE(ipAddress.lower)
    }

    else -> {
      throw IllegalArgumentException("Unsupported IP address type: ${ipAddress::class}")
    }
  }
}

fun ByteBuf.readIpRangeLE(): Pair<IPAddress, IPAddress> {
  val start = readIpLE()
  val end = readIpLE()
  return Pair(start, end)
}

fun ByteBuf.writeIpRangeLE(start: IPAddress, end: IPAddress) {
  writeIpLE(start)
  writeIpLE(end)
}

package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import io.netty.buffer.ByteBuf
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

data class ToSPacket(val confirmationKey: Byte, val tosText: String)

class ToSPacketSerializer : PacketSerializer<ToSPacket> {
  override fun serialize(packet: ToSPacket, buffer: ByteBuf) {
    buffer.writeByte(packet.confirmationKey.toInt())

    val compressedText = compressString(packet.tosText)
    buffer.writeShortLE(compressedText.size)
    buffer.writeBytes(compressedText)
  }

  private fun compressString(text: String): ByteArray {
    val baos = ByteArrayOutputStream()
    GZIPOutputStream(baos).use { gzipOut ->
      gzipOut.write(text.toByteArray(StandardCharsets.UTF_8))
    }
    return baos.toByteArray()
  }
}

class ToSPacketDeserializer : PacketDeserializer<ToSPacket> {
  override fun deserialize(buffer: ByteBuf): ToSPacket {
    val confirmationKey = buffer.readByte()
    val tosLength = buffer.readUnsignedShortLE()
    val compressedTosText = ByteArray(tosLength)
    buffer.readBytes(compressedTosText)

    val decompressedText = decompressBytes(compressedTosText)
    return ToSPacket(confirmationKey, decompressedText)
  }

  private fun decompressBytes(compressed: ByteArray): String {
    val bais = ByteArrayInputStream(compressed)
    return GZIPInputStream(bais).use { gzipIn ->
      String(gzipIn.readAllBytes(), StandardCharsets.UTF_8)
    }
  }
}

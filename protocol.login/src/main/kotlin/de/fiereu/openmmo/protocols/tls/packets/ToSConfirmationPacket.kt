package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import io.netty.buffer.ByteBuf

data class ToSConfirmationPacket(
  val confirmationKey: Byte
)

class ToSConfirmationPacketSerializer : PacketSerializer<ToSConfirmationPacket> {
  override fun serialize(packet: ToSConfirmationPacket, buffer: ByteBuf) {
    buffer.writeByte(packet.confirmationKey.toInt())
  }
}

class ToSConfirmationPacketDeserializer : PacketDeserializer<ToSConfirmationPacket> {
  override fun deserialize(buffer: ByteBuf): ToSConfirmationPacket {
    val confirmationKey = buffer.readByte()
    return ToSConfirmationPacket(confirmationKey)
  }
}
package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import io.netty.buffer.ByteBuf

data class JoinGameServerPacket(val gameServerId: UByte)

class JoinGameServerPacketSerializer : PacketSerializer<JoinGameServerPacket> {
  override fun serialize(packet: JoinGameServerPacket, buffer: ByteBuf) {
    buffer.writeByte(packet.gameServerId.toInt())
  }
}

class JoinGameServerPacketDeserializer : PacketDeserializer<JoinGameServerPacket> {
  override fun deserialize(buffer: ByteBuf): JoinGameServerPacket {
    val gameServerId = buffer.readUnsignedByte()
    return JoinGameServerPacket(gameServerId.toUByte())
  }
}

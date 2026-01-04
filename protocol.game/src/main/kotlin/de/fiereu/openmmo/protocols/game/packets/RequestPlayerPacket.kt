package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import io.netty.buffer.ByteBuf

class RequestPlayerPacket

class RequestPlayerSerializer : PacketSerializer<RequestPlayerPacket> {
  override fun serialize(packet: RequestPlayerPacket, buffer: ByteBuf) {}
}

class RequestPlayerDeserializePacket : PacketDeserializer<RequestPlayerPacket> {
  override fun deserialize(buffer: ByteBuf): RequestPlayerPacket {
    return RequestPlayerPacket()
  }
}

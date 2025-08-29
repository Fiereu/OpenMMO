package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import io.netty.buffer.ByteBuf

class RequestGameServerListPacket

class RequestGameServerListPacketSerializer : PacketSerializer<RequestGameServerListPacket> {
  override fun serialize(packet: RequestGameServerListPacket, buffer: ByteBuf) {
    // No data to serialize
  }
}

class RequestGameServerListPacketDeserializer : PacketDeserializer<RequestGameServerListPacket> {
  override fun deserialize(buffer: ByteBuf): RequestGameServerListPacket {
    // No data to deserialize
    return RequestGameServerListPacket()
  }
}

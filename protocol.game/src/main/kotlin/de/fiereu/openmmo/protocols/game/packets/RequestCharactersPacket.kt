package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import io.netty.buffer.ByteBuf

class RequestCharactersPacket()

class RequestCharactersPacketSerializer : PacketSerializer<RequestCharactersPacket> {
  override fun serialize(packet: RequestCharactersPacket, buffer: ByteBuf) {}
}

class RequestCharactersDeserializer : PacketDeserializer<RequestCharactersPacket> {
  override fun deserialize(buffer: ByteBuf): RequestCharactersPacket {
    return RequestCharactersPacket()
  }
}

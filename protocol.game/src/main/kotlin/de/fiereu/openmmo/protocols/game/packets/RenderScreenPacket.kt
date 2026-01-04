package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import io.netty.buffer.ByteBuf

data class RenderScreenPacket(val renderScreen: Boolean)

class RenderScreenSerializer : PacketSerializer<RenderScreenPacket> {
  override fun serialize(packet: RenderScreenPacket, buffer: ByteBuf) {
    buffer.writeBoolean(packet.renderScreen)
  }
}

class RenderScreenDeserializer : PacketDeserializer<RenderScreenPacket> {
  override fun deserialize(buffer: ByteBuf): RenderScreenPacket {
    return RenderScreenPacket(buffer.readBoolean())
  }
}

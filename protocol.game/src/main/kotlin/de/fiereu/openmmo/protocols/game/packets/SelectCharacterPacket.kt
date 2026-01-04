package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import io.netty.buffer.ByteBuf

data class SelectCharacterPacket(val characterId: Long, val characterIdHash: Long)

class SelectCharacterSerializer : PacketSerializer<SelectCharacterPacket> {
  override fun serialize(packet: SelectCharacterPacket, buffer: ByteBuf) {
    buffer.apply {
      buffer.writeLongLE(packet.characterId)
      buffer.writeLongLE(packet.characterIdHash)
    }
  }
}

class SelectCharacterDeserializer : PacketDeserializer<SelectCharacterPacket> {
  override fun deserialize(buffer: ByteBuf): SelectCharacterPacket =
      buffer.run {
        val characterId = buffer.readLong()
        val characterIdHash = buffer.readLong()
        return SelectCharacterPacket(characterId, characterIdHash)
      }
}

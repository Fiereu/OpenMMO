package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.common.CharacterInfo
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.game.packets.codecs.readCharacterInfoLE
import de.fiereu.openmmo.protocols.game.packets.codecs.writeCharacterInfoLE
import io.netty.buffer.ByteBuf

data class SelectedCharacterPacket(val character: CharacterInfo?)

class SelectedCharacterSerializer : PacketSerializer<SelectedCharacterPacket> {
  override fun serialize(packet: SelectedCharacterPacket, buffer: ByteBuf) {
    buffer.apply {
      val hasCharacter = packet.character != null
      writeBoolean(hasCharacter)
      if (!hasCharacter) return
      writeCharacterInfoLE(false, packet.character)
    }
  }
}

class SelectedCharacterDeserializer : PacketDeserializer<SelectedCharacterPacket> {
  override fun deserialize(buffer: ByteBuf): SelectedCharacterPacket =
      buffer.run {
        val hasCharacter = readBoolean()
        val character = if (hasCharacter) readCharacterInfoLE(false) else null
        return SelectedCharacterPacket(character)
      }
}

package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.common.CharacterInfo
import de.fiereu.openmmo.common.Pokemon
import de.fiereu.openmmo.common.enums.SkinSlot
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.game.packets.codecs.*
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

data class CharacterListPacket(val characters: List<CharacterEntry>)

data class CharacterEntry(
    val characterInfo: CharacterInfo,
    val skinSet: SkinSet,
    val guildId: Int? = null,
    val pokemon: List<Pokemon>
) {
  init {
    require(pokemon.size <= 6) { "Party size can't be more than 6 pokemon" }
  }
}

class CharacterListSerializer : PacketSerializer<CharacterListPacket> {
  override fun serialize(packet: CharacterListPacket, buffer: ByteBuf) =
      buffer.run {
        writeByte(packet.characters.size)
        packet.characters.forEach { characterEntry ->
          writeCharacterInfoLE(useLong = false, characterEntry.characterInfo)
          writeSkinsLE(characterEntry.skinSet)
          // TODO find out why we send 2 times the skin data
          writeSkinsLE(characterEntry.skinSet, writeByte = false)

          val hasGuild = characterEntry.guildId != null
          buffer.writeBoolean(hasGuild)
          if (hasGuild) {
            writeUtf16LE("") // unused
            writeIntLE(characterEntry.guildId)
          }

          buffer.writeByte(characterEntry.pokemon.size)
          characterEntry.pokemon.forEach(::writePokemonLE)
        }
      }
}

class CharacterListDeserializer : PacketDeserializer<CharacterListPacket> {
  override fun deserialize(buffer: ByteBuf): CharacterListPacket =
      buffer.run {
        val size = buffer.readByte().toInt()
        val characters =
            buildList(size) {
              val characterInfo = readCharacterInfoLE(useLong = true)
              val skinSet1 = readSkinsLE(SkinSlot.entries, readByte = true)
              val skinSet2 = readSkinsLE(SkinSlot.entries, readByte = false)

              val guildId =
                  if (readBoolean()) {
                    readUtf16LE() // unused
                    readIntLE()
                  } else null

              val partySize = readUnsignedByte().toInt()
              val pokemon = buildList(partySize) { repeat(partySize) { add(readPokemonLE()) } }
              add(CharacterEntry(characterInfo, skinSet1, guildId, pokemon))
            }
        return CharacterListPacket(characters)
      }
}

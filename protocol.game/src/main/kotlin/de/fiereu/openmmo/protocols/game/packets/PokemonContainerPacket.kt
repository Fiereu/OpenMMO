package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.common.Pokemon
import de.fiereu.openmmo.common.enums.PokemonContainer
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.game.packets.codecs.readPokemonLE
import de.fiereu.openmmo.protocols.game.packets.codecs.writePokemonLE
import io.netty.buffer.ByteBuf

data class PokemonContainerPacket(
    val container: PokemonContainer,
    val hasChange: Boolean,
    val delete: Boolean,
    val pokemon: List<Pokemon>,
) {
  init {
    if (delete)
        require(hasChange) {
          "Pokemon container cannot be deleted if the packet doesn't change anything"
        }
    if (delete)
        require(pokemon.isEmpty()) { "Deleting a Pokemon container shouldn't have any pokemon" }
  }
}

class PokemonContainerSerializer : PacketSerializer<PokemonContainerPacket> {
  override fun serialize(packet: PokemonContainerPacket, buffer: ByteBuf) =
      buffer.run {
        writeByte(packet.container.ordinal)
        var flags = 0
        if (packet.hasChange) flags = flags or 1
        if (packet.delete) flags = flags or 2
        writeByte(flags)
        if (packet.delete) return
        writeByte(packet.pokemon.size)
        for (pokemon in packet.pokemon) {
          writePokemonLE(pokemon)
        }
      }
}

class PokemonContainerDeserializer : PacketDeserializer<PokemonContainerPacket> {
  override fun deserialize(buffer: ByteBuf): PokemonContainerPacket =
      buffer.run {
        val pokemonContainer = PokemonContainer.entries[buffer.readByte().toInt()]
        val flags = buffer.readByte().toInt()
        val hasChange = (flags and 1) != 0
        val delete = (flags and 2) != 0
        val pokemon = buildList<Pokemon>(readByte().toInt()) { readPokemonLE() }
        return PokemonContainerPacket(pokemonContainer, hasChange, delete, pokemon)
      }
}

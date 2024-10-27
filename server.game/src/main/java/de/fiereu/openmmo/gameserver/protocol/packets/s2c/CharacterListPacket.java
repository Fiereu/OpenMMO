package de.fiereu.openmmo.gameserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.CharacterRecord;
import de.fiereu.openmmo.db.jooq.tables.records.PokemonRecord;
import de.fiereu.openmmo.gameserver.codecs.Codecs;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CharacterListPacket extends OutgoingPacket {
  private final Map<CharacterRecord, List<PokemonRecord>> characterPokemonMap;

  public CharacterListPacket() {
    this.characterPokemonMap = Map.of();
  }

  @Override
  public void encode(ByteBufEx buffer) throws Exception {
    buffer.writeByte(characterPokemonMap.size());
    for (Map.Entry<CharacterRecord, List<PokemonRecord>> entry : characterPokemonMap.entrySet()) {
      CharacterRecord character = entry.getKey();
      List<PokemonRecord> pokemon = entry.getValue();
      Codecs.CHARACTER_CODEC.encode(buffer, character);
      Codecs.SKIN_CODEC_1.encode(buffer, character);
      Codecs.SKIN_CODEC_2.encode(buffer, character);
      Codecs.CHARACTER_GUILD_CODEC.encode(buffer, character);

      buffer.writeByte(pokemon.size());
      for (PokemonRecord pkm : pokemon) {
        Codecs.POKEMON_CODEC.encode(buffer, pkm);
      }
    }
  }
}

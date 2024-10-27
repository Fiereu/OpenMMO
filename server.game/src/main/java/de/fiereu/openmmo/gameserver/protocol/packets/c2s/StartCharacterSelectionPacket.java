package de.fiereu.openmmo.gameserver.protocol.packets.c2s;

import com.google.inject.Inject;
import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.CharacterRecord;
import de.fiereu.openmmo.db.jooq.tables.records.ContainerRecord;
import de.fiereu.openmmo.db.jooq.tables.records.PokemonRecord;
import de.fiereu.openmmo.gameserver.protocol.GameProtocol;
import de.fiereu.openmmo.gameserver.protocol.packets.s2c.CharacterListPacket;
import de.fiereu.openmmo.gameserver.services.CharacterService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartCharacterSelectionPacket extends IncomingPacket {
  @Inject
  private CharacterService characterService;

  @Override
  public void decode(ByteBufEx buffer) {}

  @Override
  public void handle(Session session) throws Exception {
    Integer userId = session.attr(GameProtocol.ATTRIBUTE_USER_ID).get();
    if (userId == null) {
      session.close();
      return;
    }

    List<CharacterRecord> characters = characterService.getCharacters(userId);
    if (characters.isEmpty()) {
      session.send(new CharacterListPacket());
      return;
    }

    Map<CharacterRecord, List<PokemonRecord>> characterPokemonMap = new HashMap<>();
    for (CharacterRecord character : characters) {
      ContainerRecord partyContainer = characterService.getPartyContainer();
      characterPokemonMap.put(character, characterService.getPokemonByContainerAndCharacter(character.getId(), partyContainer));
    }


    session.send(new CharacterListPacket(characterPokemonMap));
  }
}

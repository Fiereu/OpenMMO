package de.fiereu.openmmo.gameserver.services;

import de.fiereu.openmmo.db.Database;
import de.fiereu.openmmo.db.jooq.tables.records.*;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static de.fiereu.openmmo.db.jooq.Tables.*;

@RequiredArgsConstructor
public class CharacterService {

  private final Database database;

  public List<CharacterRecord> getCharacters(int userId) {
    return database.ctx()
        .select().from(CHARACTER)
        .where(CHARACTER.USER_ID.eq(userId))
        .fetchInto(CharacterRecord.class);
  }

  public ContainerRecord getPartyContainer() {
    return database.ctx()
        .select().from(CONTAINER)
        .where(CONTAINER.NAME.eq("party"))
        .fetchOneInto(ContainerRecord.class);
  }

  public List<PokemonRecord> getPokemonByContainerAndCharacter(int characterId, ContainerRecord container) {
    return database.ctx()
        .select().from(POKEMON)
        .where(POKEMON.TRAINER_ID.eq(characterId))
        .and(POKEMON.CONTAINER_ID.eq(container.getId()))
        .fetchInto(PokemonRecord.class);
  }

  public CharacterRecord getCharacter(int characterId) {
    return database.ctx()
        .select().from(CHARACTER)
        .where(CHARACTER.ID.eq(characterId))
        .fetchOneInto(CharacterRecord.class);
  }

  public List<ContainerRecord> getPokemonContainer() {
    return database.ctx()
        .select().from(CONTAINER)
        .fetchInto(ContainerRecord.class);
  }

  public List<InventoryRecord> getInventories() {
    return database.ctx()
        .select().from(INVENTORY)
        .fetchInto(InventoryRecord.class);
  }

  public List<OwnedItemRecord> getItemsByContainerAndCharacter(int characterId, InventoryRecord inventory) {
    return database.ctx()
        .select().from(OWNED_ITEM)
        .where(OWNED_ITEM.OWNER_ID.eq(characterId))
        .and(OWNED_ITEM.INVENTORY_ID.eq(inventory.getId()))
        .fetchInto(OwnedItemRecord.class);
  }

  public InventoryRecord getInventory() {
    return database.ctx()
        .select().from(INVENTORY)
        .where(INVENTORY.NAME.eq("inventory"))
        .fetchOneInto(InventoryRecord.class);
  }
}

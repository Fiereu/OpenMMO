package de.fiereu.openmmo.gameserver.protocol.packets.c2s;

import com.google.inject.Inject;
import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.CharacterRecord;
import de.fiereu.openmmo.db.jooq.tables.records.ContainerRecord;
import de.fiereu.openmmo.db.jooq.tables.records.InventoryRecord;
import de.fiereu.openmmo.gameserver.game.*;
import de.fiereu.openmmo.gameserver.game.chat.ChatMessage;
import de.fiereu.openmmo.gameserver.protocol.GameProtocol;
import de.fiereu.openmmo.gameserver.protocol.packets.s2c.*;
import de.fiereu.openmmo.gameserver.services.CharacterService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SelectCharacterPacket extends IncomingPacket {
    @Inject
    private CharacterService characterService;
    private int characterId;
    /**
     * A hash of the characterId. This is used to verify that the client knows the characterId.
     * The hash is generated by the native module in the PokeMMO client as a kind of security measure.
     */
    private long characterIdHash;
    @Override
    public void decode(ByteBufEx buffer) {
        this.characterId = buffer.readIntLE();
        this.characterIdHash = buffer.readLongLE();
    }

    @Override
    public void handle(Session session) throws Exception {
        // TODO: compare characterId with characterIdHash

        int userId = session.attr(GameProtocol.ATTRIBUTE_USER_ID).get();
        CharacterRecord character = characterService.getCharacter(characterId);
        if (character == null || character.getUserId() != userId) {
            log.warn("User {} tried to select character {} which does not exist or does not belong to him", userId, characterId);
            session.send(new AcknowledgeCharacterSelectionPacket());
            session.close();
            return;
        }

        session.attr(GameProtocol.ATTRIBUTE_CHARACTER).set(character);

        // send all required pokemon containers
        characterService.getPokemonContainer().stream()
                .filter(ContainerRecord::getRequired)
                .map(container -> new SendPokemonContainerPacket(container, characterService.getPokemonByContainerAndCharacter(character.getId(), container)))
                .forEach(session::send);

        // we should probably only send inventory/bag
        /*
        characterService.getInventory().stream()
                .map(container -> new SendInventoryPacket(container, characterService.getItemsByContainerAndCharacter(character.getId(), container)))
                .forEach(session::send);
         */
        InventoryRecord inventory = characterService.getInventory();
        session.send(new SendInventoryPacket(inventory, characterService.getItemsByContainerAndCharacter(character.getId(), inventory)));

        Tile2D[] tiles = new Tile2D[4];
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new Tile2D((short) 8, (byte) 0);
        }

        session.send(
                new AcknowledgeCharacterSelectionPacket(character), // Needs party pokemon container first to render HudGUI
                new ChatMessagePacket(ChatMessage.gameNotification("Welcome to OpenMMO!")),
                new LoadMapPacket(true, true, (byte) 0, (byte) 6, (byte) 6,
                        (byte) 2, (byte) 2, tiles,
                        15, 10, 12, 14,
                        Lighting.REGULAR, Weather.IN_HOUSE_WEATHER, Location.INSIDE, EncounterType.RANDOM)
        );

        // Following are the packets that are send to the client after the character is selected
        // START
        // 0x72: a single short value 0
        // 0x13: 4 times this packet.
        // 0x40 IMPLEMENTED
        // 0x55: 3 bytes all 0
        // 0x67: 2 bytes all 0
        // 0x63: 2 bytes all 0
        // 0x98: 6 bytes all 0
        // 0x0A
        // 0x29: 6 times
        // 0x1C: 2 bytes all 0
        // 0x04 IMPLEMENTED
        // 0xF1
        // 0x4A
        // 0x4D
        // 0x6D
        // 0x09: welcome message IMPLEMENTED
        // 0xF5
        // 0x4F
        // 0xD3
        // 0xFC
        // 0x59: 2 times
        // 0x6E
        // 0xB9
        // 0x10 IMPLEMENTED
        // C2S 0x05 IMPLEMENTED
        // 0x05: IMPLEMENTED
        // 0x90
        // 0x12: 2 times
        // 0xB4 IMPLEMENTED
        // END

    }
}
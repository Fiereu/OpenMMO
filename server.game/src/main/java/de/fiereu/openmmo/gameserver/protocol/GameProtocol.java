package de.fiereu.openmmo.gameserver.protocol;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.fiereu.openmmo.DataFlow;
import de.fiereu.openmmo.Protocol;
import de.fiereu.openmmo.db.jooq.tables.records.CharacterRecord;
import de.fiereu.openmmo.gameserver.protocol.packets.c2s.*;
import de.fiereu.openmmo.gameserver.protocol.packets.s2c.*;
import de.fiereu.openmmo.gameserver.services.CharacterService;
import de.fiereu.openmmo.services.SessionService;
import io.netty.util.AttributeKey;

public class GameProtocol extends Protocol {
  public static final AttributeKey<Integer> ATTRIBUTE_USER_ID = AttributeKey.valueOf("userId");
  public static final AttributeKey<CharacterRecord> ATTRIBUTE_CHARACTER = AttributeKey.valueOf("character");

  public GameProtocol(SessionService sessionService, CharacterService characterService) {
    super((byte) 2, true, true, createInjector(sessionService, characterService));

    registerPacket(DataFlow.CLIENT_TO_SERVER, (byte) 0x01, JoinPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x01, JoinResponsePacket.class);
    registerPacket(DataFlow.CLIENT_TO_SERVER, (byte) 0x02, StartCharacterSelectionPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x02, CharacterListPacket.class);
    registerPacket(DataFlow.CLIENT_TO_SERVER, (byte) 0x04, SelectCharacterPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x04, AcknowledgeCharacterSelectionPacket.class);
    registerPacket(DataFlow.CLIENT_TO_SERVER, (byte) 0x05, RequestPlayerPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x05, LoadPlayerPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x09, ChatMessagePacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x10, LoadMapPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x13, SendPokemonContainerPacket.class);
    registerPacket(DataFlow.CLIENT_TO_SERVER, (byte) 0x20, HeartbeatPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x40, SendInventoryPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0xB4, RenderScreenPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0xF3, InitializeGamePacket.class);
  }

  private static Injector createInjector(SessionService sessionService, CharacterService characterService) {
    return Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(SessionService.class).toInstance(sessionService);
        bind(CharacterService.class).toInstance(characterService);
      }
    });
  }
}

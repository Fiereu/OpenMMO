package de.fiereu.openmmo.gameserver.protocol.packets.c2s;

import com.google.inject.Inject;
import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.gameserver.protocol.GameProtocol;
import de.fiereu.openmmo.gameserver.protocol.packets.s2c.InitializeGamePacket;
import de.fiereu.openmmo.gameserver.protocol.packets.s2c.JoinResponsePacket;
import de.fiereu.openmmo.services.SessionService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SuppressWarnings({"unused", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection"})
public class JoinPacket extends IncomingPacket {
  @Getter
  @RequiredArgsConstructor
  private static class RomInfo {
    private final String code;
    private final byte id;
    private final byte type; // maybe?
  }

  @Inject
  private SessionService sessionService;

  private byte option; // unknown
  private int userId;
  private byte[] sessionKey;
  private byte[] mac;
  private int clientRevision; // hardcoded into the client
  private int installationRevision; // revision.txt in the game directory
  private byte clientLanguage; // language the client is set to
  private short chatLanguages; // bitmask of languages the client supports
  private short ignoreLanguages; // bitmask of languages the client ignores
  private byte romMask; // bitmask of roms that are loaded by the client used for region checks
  private List<RomInfo> roms;
  private Map<Byte, String> clientInfo;
  private byte platform; // TODO: introduce a platform enum for this and next
  /**
   * Enum:
   *  Windows = 0,
   *  Linux = 1,
   *  MacOsX = 2,
   *  Android = 3,
   *  IOS = 4
   */
  private byte gdxPlatform; // 2 ways of receiving the platform
  /**
   * Enum:
   *  _32 = 0,
   *  _64 = 1,
   *  _128 = 2
   */
  private byte bitness;
  private byte unk1;
  private byte[] unk2;

  @Override
  public void decode(ByteBufEx buffer) {
    option = buffer.readByte();
    userId = buffer.readIntLE();
    sessionKey = new byte[buffer.readByte()];
    buffer.readBytes(sessionKey);
    mac = buffer.readByteArray(6);
    clientRevision = buffer.readIntLE();
    installationRevision = buffer.readIntLE();
    clientLanguage = buffer.readByte();
    chatLanguages = buffer.readShortLE();
    ignoreLanguages = buffer.readShortLE();
    romMask = buffer.readByte();
    roms = new ArrayList<>();
    int romCount = buffer.readByte();
    for (int i = 0; i < romCount; i++) {
      roms.add(new RomInfo(buffer.readUtf16LE(), buffer.readByte(), buffer.readByte()));
    }

    clientInfo = new HashMap<>();
    int clientInfoCount = buffer.readByte();
    for (int i = 0; i < clientInfoCount; i++) {
      clientInfo.put(buffer.readByte(), buffer.readUtf16LE());
    }

    platform = buffer.readByte();
    gdxPlatform = buffer.readByte();
    bitness = buffer.readByte();
    unk1 = buffer.readByte();
    unk2 = buffer.readByteArray(32); // smth related to soundmuxer???
  }

  @Override
  public void handle(Session session) throws Exception {
    if (!sessionService.validateSessionKey(userId, session.getRemoteAddress(), sessionKey)) {
      session.send(new JoinResponsePacket(false));
      session.close();
    }

    session.attr(GameProtocol.ATTRIBUTE_USER_ID).set(userId);
    log.debug("User {} authenticated.", userId);

    session.send(new JoinResponsePacket(true));
    session.send(new InitializeGamePacket());
  }
}

package de.fiereu.openmmo.loginserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.loginserver.service.ServerService;

import java.util.List;

public class GameServerListPacket extends OutgoingPacket {
  private final List<ServerService.GameServer> gameServers;

  public GameServerListPacket(List<ServerService.GameServer> gameServers) {
    this.gameServers = gameServers;
  }

  @Override
  public void encode(ByteBufEx buffer) throws Exception {
    assert gameServers.size() <= Byte.MAX_VALUE;

    buffer.writeByte(gameServers.size());

    if (gameServers.isEmpty()) {
      buffer.writeByte(0);
      buffer.writeByte(0);
      return;
    }

    ServerService.GameServer first = gameServers.getFirst();
    buffer.writeByte(first.getId());

    for (ServerService.GameServer gameServer : gameServers) {
      buffer.writeByte(gameServer.getId());
      buffer.writeUtf16LE(gameServer.getName());
      buffer.writeShortLE(0); // current players
      buffer.writeShortLE(0); // max players
      buffer.writeBoolean(gameServer.isJoinable());
    }
  }
}
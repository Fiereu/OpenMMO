package de.fiereu.openmmo.loginserver.protocol.packets.c2s;

import com.google.inject.Inject;
import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.loginserver.LoginState;
import de.fiereu.openmmo.loginserver.protocol.LoginProtocol;
import de.fiereu.openmmo.loginserver.protocol.packets.s2c.GameServerListPacket;
import de.fiereu.openmmo.loginserver.service.ServerService;

import java.util.List;

public class RequestGameServerListPacket extends IncomingPacket {
  @Inject
  ServerService serverService;

  @Override
  public void decode(ByteBufEx buffer) {
  }

  @Override
  public void handle(Session session) throws Exception {
    if (session.attr(LoginProtocol.ATTRIBUTE_LOGIN_STATE).get() != LoginState.AUTHED) {
      throw new IllegalStateException("Not authenticated");
    }

    Integer userId = session.attr(LoginProtocol.ATTRIBUTE_USER_ID).get();
    if (userId == null) {
      throw new IllegalStateException("User ID not set");
    }

    List<ServerService.GameServer> gameServers = serverService.getGameServers(userId);

    session.send(new GameServerListPacket(gameServers));
  }
}
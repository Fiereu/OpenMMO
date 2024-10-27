package de.fiereu.openmmo.loginserver.protocol.packets.c2s;

import com.github.maltalex.ineter.base.IPAddress;
import com.google.inject.Inject;
import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.loginserver.LoginState;
import de.fiereu.openmmo.loginserver.protocol.LoginProtocol;
import de.fiereu.openmmo.loginserver.protocol.packets.s2c.GameServerNodesPacket;
import de.fiereu.openmmo.loginserver.service.ServerService;
import de.fiereu.openmmo.services.SessionService;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;

@Slf4j
public class JoinGameServerPacket extends IncomingPacket {
  @Inject
  private ServerService serverService;
  @Inject
  private SessionService sessionService;
  private byte gameServerId;

  @Override
  public void decode(ByteBufEx buffer) {
    gameServerId = buffer.readByte();
  }

  @Override
  public void handle(Session session) throws Exception {
    LoginState loginState = session.attr(LoginProtocol.ATTRIBUTE_LOGIN_STATE).get();
    if (loginState != LoginState.AUTHED) {
      session.send(new GameServerNodesPacket(loginState));
      return;
    }

    int userId = session.attr(LoginProtocol.ATTRIBUTE_USER_ID).get();
    Optional<ServerService.GameServer> gameServerOpt = serverService.getGameServer(gameServerId, userId);
    if (gameServerOpt.isEmpty()) {
      log.error("User {} tried to join game server {} but server is down", userId, gameServerId);
      session.send(new GameServerNodesPacket(LoginState.SERVER_DOWN));
      return;
    }

    ServerService.GameServer gameServer = gameServerOpt.get();
    if (!gameServer.isJoinable()) {
      log.error("User {} tried to join game server {} but has insufficient permissions", userId, gameServerId);
      session.send(new GameServerNodesPacket(LoginState.SYSTEM_ERROR));
      return;
    }

    byte[] sessionKey = sessionService.generateSessionKey(gameServerId, userId, session.getRemoteAddress());
    if (sessionKey == null) {
      log.error("User {} tried to join game server {} but failed to generate session key", userId, gameServerId);
      session.send(new GameServerNodesPacket(LoginState.SYSTEM_ERROR));
      return;
    }

    InetAddress localAddress = InetAddress.getLocalHost();
    List<ServerService.GameServerNode> gameServerNodes = serverService.getGameServerNodes(gameServerId);
    session.send(new GameServerNodesPacket(
        LoginState.AUTHED,
        gameServerId,
        userId,
        sessionKey,
        IPAddress.of(localAddress),
        localAddress.getHostName(),
        gameServer.getPort(),
        gameServerNodes
    ));
  }
}
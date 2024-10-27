package de.fiereu.openmmo.loginserver.protocol;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.fiereu.openmmo.DataFlow;
import de.fiereu.openmmo.Protocol;
import de.fiereu.openmmo.loginserver.LoginState;
import de.fiereu.openmmo.loginserver.protocol.packets.c2s.JoinGameServerPacket;
import de.fiereu.openmmo.loginserver.protocol.packets.c2s.LoginPacket;
import de.fiereu.openmmo.loginserver.protocol.packets.c2s.RequestGameServerListPacket;
import de.fiereu.openmmo.loginserver.protocol.packets.s2c.GameServerListPacket;
import de.fiereu.openmmo.loginserver.protocol.packets.s2c.GameServerNodesPacket;
import de.fiereu.openmmo.loginserver.protocol.packets.s2c.LoginResultPacket;
import de.fiereu.openmmo.loginserver.service.LoginService;
import de.fiereu.openmmo.loginserver.service.ServerService;
import de.fiereu.openmmo.services.SessionService;
import io.netty.util.AttributeKey;

public class LoginProtocol extends Protocol {
  public static final AttributeKey<LoginState> ATTRIBUTE_LOGIN_STATE = AttributeKey.valueOf("login_state");
  public static final AttributeKey<Integer> ATTRIBUTE_USER_ID = AttributeKey.valueOf("user_id");

  public LoginProtocol(LoginService loginService, ServerService serverService, SessionService sessionService) {
    super((byte) 16, true, false, createInjector(loginService, serverService, sessionService));

    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x01, LoginResultPacket.class);
    registerPacket(DataFlow.CLIENT_TO_SERVER, (byte) 0x02, RequestGameServerListPacket.class);
    registerPacket(DataFlow.CLIENT_TO_SERVER, (byte) 0x03, JoinGameServerPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x03, GameServerNodesPacket.class);
    registerPacket(DataFlow.CLIENT_TO_SERVER, (byte) 0x11, LoginPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x22, GameServerListPacket.class);
  }

  private static Injector createInjector(LoginService loginService, ServerService serverService, SessionService sessionService) {
    return Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(LoginService.class).toInstance(loginService);
        bind(ServerService.class).toInstance(serverService);
        bind(SessionService.class).toInstance(sessionService);
      }
    });
  }
}

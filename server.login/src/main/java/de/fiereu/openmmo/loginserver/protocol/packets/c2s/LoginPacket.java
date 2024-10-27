package de.fiereu.openmmo.loginserver.protocol.packets.c2s;

import com.google.inject.Inject;
import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.loginserver.LoginState;
import de.fiereu.openmmo.loginserver.protocol.LoginProtocol;
import de.fiereu.openmmo.loginserver.protocol.packets.s2c.LoginResultPacket;
import de.fiereu.openmmo.loginserver.service.LoginService;

import java.util.Optional;

public class LoginPacket extends IncomingPacket {
  @Inject
  private LoginService loginService;

  private String username;
  private boolean manualLogin;
  private byte[] hwid;
  private boolean useToken;
  private String password;
  private boolean stayLoggedIn;
  private byte[] token;
  private String language;
  private int clientRevision;
  private int gameRevision;
  private int os;
  private byte[] unknown0;

  @Override
  public void decode(ByteBufEx buffer) {
    username = buffer.readUtf16LE();
    manualLogin = buffer.readBoolean();
    hwid = new byte[buffer.readUnsignedByte()];
    buffer.readBytes(hwid);
    useToken = buffer.readBoolean();
    if (useToken) {
      token = new byte[buffer.readUnsignedByte()];
      buffer.readBytes(token);
    } else {
      password = buffer.readUtf16LE();
      stayLoggedIn = buffer.readBoolean();
    }
    language = buffer.readUtf16LE();
    clientRevision = buffer.readIntLE();
    gameRevision = buffer.readIntLE();
    os = buffer.readUnsignedByte();
    unknown0 = new byte[buffer.readUnsignedByte()];
    buffer.readBytes(unknown0);
  }

  @Override
  public void handle(Session session) throws Exception {
    try {
      Optional<Integer> userIdOpt = loginService.getAccountId(username);
      if (userIdOpt.isEmpty()) {
        throw new IllegalStateException("User not found");
      }

      LoginState state;
      int userId = userIdOpt.get();
      if (useToken) {
        throw new UnsupportedOperationException("Token login not supported");
      } else {
        state = loginService.login(userId, password);
      }

      session.attr(LoginProtocol.ATTRIBUTE_LOGIN_STATE).set(state);
      if (state == LoginState.AUTHED) {
        session.attr(LoginProtocol.ATTRIBUTE_USER_ID).set(userId);
      } else {
        session.attr(LoginProtocol.ATTRIBUTE_USER_ID).set(null);
      }

      session.send(new LoginResultPacket(state));
    } catch (Exception e) {
      session.attr(LoginProtocol.ATTRIBUTE_LOGIN_STATE).set(LoginState.UNAUTHED);
      session.attr(LoginProtocol.ATTRIBUTE_USER_ID).set(null);
      session.send(new LoginResultPacket(LoginState.SYSTEM_ERROR));
    }
  }
}

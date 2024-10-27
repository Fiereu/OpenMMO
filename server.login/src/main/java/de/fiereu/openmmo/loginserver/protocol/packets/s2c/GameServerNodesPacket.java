package de.fiereu.openmmo.loginserver.protocol.packets.s2c;

import com.github.maltalex.ineter.base.IPAddress;
import com.github.maltalex.ineter.base.IPv4Address;
import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.loginserver.LoginState;
import de.fiereu.openmmo.loginserver.service.ServerService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GameServerNodesPacket extends OutgoingPacket {
  private final LoginState loginState;
  private final byte gameServerId;
  private final int accountId;
  private final byte[] sessionKey;
  private final IPAddress localAddress; // 127.0.0.1
  private final String localHostname; // localhost
  private final int port; // server port not node port (7777)
  private final List<ServerService.GameServerNode> nodes;

  public GameServerNodesPacket(LoginState loginState) {
    if (loginState == LoginState.AUTHED) {
      throw new IllegalArgumentException("Use the other constructor for AUTHED state");
    }

    this.loginState = loginState;
    this.gameServerId = 0;
    this.accountId = 0;
    this.sessionKey = new byte[0];
    this.localAddress = IPv4Address.MIN_ADDR;
    this.localHostname = "";
    this.port = 0;
    this.nodes = List.of();
  }

  @Override
  public void encode(ByteBufEx buffer) throws Exception {
    buffer.writeByte(loginState.getId());
    if (loginState != LoginState.AUTHED) {
      return;
    }

    buffer.writeIntLE(accountId);
    buffer.writeByte(sessionKey.length);
    buffer.writeBytes(sessionKey);

    buffer.writeByte(gameServerId);

    byte[] localAddressBytes = localAddress.toLittleEndianArray();
    buffer.writeByte(localAddressBytes.length);
    buffer.writeBytes(localAddressBytes);
    buffer.writeUtf16LE(localHostname);

    buffer.writeIntLE(port);

    assert nodes.size() <= Byte.MAX_VALUE;
    buffer.writeByte(nodes.size());
    for (ServerService.GameServerNode node : nodes) {
      buffer.writeByte(0); // unused
      buffer.writeIpLE(node.getAddress4());
      buffer.writeIpLE(node.getAddress6());
      buffer.writeShortLE(node.getPort());
      buffer.writeByte(node.getId());
    }
  }
}

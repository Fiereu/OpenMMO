package de.fiereu.openmmo.gameserver.protocol.packets.c2s;

import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.gameserver.protocol.GameProtocol;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatPacket extends IncomingPacket {
  private int vmFlags; // is 0 if no vm/hypervisor is detected
  private byte[] unk1;
  private short unk2;
  private long clientMillis;
  private long clientNanos;
  private byte[] mac;
  private byte unk3; // should always be 0

  @Override
  public void decode(ByteBufEx buffer) {
    vmFlags = buffer.readIntLE();
    unk1 = buffer.readByteArray(4);
    unk2 = buffer.readShortLE();
    clientMillis = buffer.readLongLE();
    clientNanos = buffer.readLongLE();
    mac = buffer.readByteArray(6);
    unk3 = buffer.readByte();
  }

  @Override
  public void handle(Session session) throws Exception {
    log.trace("Received heartbeat from user {}. ", session.attr(GameProtocol.ATTRIBUTE_USER_ID).get());

    // TODO: implement vm detection (low priority ig)
    // TODO: make sure the client is sending the HeartbeatPacket at a regular interval else kick
  }
}

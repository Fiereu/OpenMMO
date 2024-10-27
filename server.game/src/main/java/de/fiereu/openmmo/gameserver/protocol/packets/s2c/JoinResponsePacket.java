package de.fiereu.openmmo.gameserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import lombok.RequiredArgsConstructor;

public class JoinResponsePacket extends OutgoingPacket {
  private final boolean success;
  private final byte unk1;
  private final int unk2;
  private final int unk3;
  private final int unk4;
  private final int unk5; // a timestamp of smth
  private final int unk6; // current timestamp

  public JoinResponsePacket(boolean success) {
    this.success = success;
    this.unk1 = 0;
    this.unk2 = 0;
    this.unk3 = 0;
    this.unk4 = 0;
    this.unk5 = 0;
    this.unk6 = (int) System.currentTimeMillis() / 1000;
  }


  @Override
  public void encode(ByteBufEx buffer) throws Exception {
    buffer.writeBoolean(success);
    if (!success) return;

    buffer.writeUtf16LE(""); // unused since rev. 22215
    buffer.writeByte(unk1);
    buffer.writeIntLE(unk2);
    buffer.writeIntLE(unk3);
    buffer.writeIntLE(unk4);
    buffer.writeIntLE(unk5);
    buffer.writeIntLE(unk6);
  }
}

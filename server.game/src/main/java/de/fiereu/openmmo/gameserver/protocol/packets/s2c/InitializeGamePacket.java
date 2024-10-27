package de.fiereu.openmmo.gameserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public class InitializeGamePacket extends OutgoingPacket {
  // This packet also triggers the title scrambling
  // we update the inner item database of the client
  @Getter
  @RequiredArgsConstructor
  private static class UnknownType {
    private final short unk1;
    private final byte unk2;
  }
  private final byte unk1;
  private final short unk2;
  private final float unk3;
  private final short unk4;
  private final short unk5;
  private final short unk6;
  private final int unk7;
  private final byte unk8;
  private final byte flagUnk9;
  private final short[] unk10;
  private final byte[] unk11;
  private final short[] unk12;
  private final short[] unk13;
  private final UnknownType[] unk14;

  public InitializeGamePacket() {
    this.unk1 = 75;
    this.unk2 = 2500;
    this.unk3 = 0.5f;
    this.unk4 = 100;
    this.unk5 = 1000;
    this.unk6 = 2500;
    this.unk7 = 25000;
    this.unk8 = 5;
    this.flagUnk9 = 1 | 1 << 1 | 1 << 3 | 1 << 4 | 1 << 6;
    this.unk10 = new short[0];
    this.unk11 = new byte[0];
    this.unk12 = new short[0];
    this.unk13 = new short[0];
    this.unk14 = new UnknownType[0];
  }

  @Override
  public void encode(ByteBufEx buffer) throws Exception {
    buffer.writeByte(unk1);
    buffer.writeShortLE(unk2);
    buffer.writeFloatLE(unk3);
    buffer.writeShortLE(unk4);
    buffer.writeShortLE(unk5);
    buffer.writeShortLE(unk6);
    buffer.writeIntLE(unk7);
    buffer.writeByte(unk8);
    buffer.writeByte(flagUnk9);
    buffer.writeShortLE(unk10.length);
    for (short unk : unk10) {
      buffer.writeShortLE(unk);
    }

    buffer.writeByte(unk11.length);
    for (byte unk : unk11) {
      buffer.writeByte(unk);
    }

    buffer.writeShortLE(unk12.length);
    for (short unk : unk12) {
      buffer.writeShortLE(unk);
    }

    buffer.writeShortLE(unk13.length);
    for (short unk : unk13) {
      buffer.writeShortLE(unk);
    }

    buffer.writeShortLE(unk14.length);
    for (UnknownType unk : unk14) {
      buffer.writeShortLE(unk.unk1);
      buffer.writeByte(unk.unk2);
    }
  }
}

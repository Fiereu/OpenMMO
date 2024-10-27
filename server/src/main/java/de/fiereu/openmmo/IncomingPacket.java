package de.fiereu.openmmo;

import de.fiereu.openmmo.bytes.ByteBufEx;

public abstract class IncomingPacket extends Packet {
  public IncomingPacket() {
    super();
  }

  @Override
  public void encode(ByteBufEx buffer) throws Exception {
    throw new UnsupportedOperationException();
  }
}

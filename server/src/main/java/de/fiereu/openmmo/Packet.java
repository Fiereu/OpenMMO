package de.fiereu.openmmo;

import de.fiereu.openmmo.bytes.ByteBufEx;

public abstract class Packet {
  public Packet() {}
  abstract public void encode(ByteBufEx buffer) throws Exception;
  abstract public void decode(ByteBufEx buffer);
  abstract public void handle(Session session) throws Exception;
}

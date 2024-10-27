package de.fiereu.openmmo;

import com.google.inject.Injector;
import de.fiereu.openmmo.bytes.ByteBufEx;
import io.netty.buffer.Unpooled;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Protocol {
  private final Map<DataFlow, Map<Byte, Class<? extends Packet>>> packets = new HashMap<>();
  private final Map<DataFlow, Map<Class<? extends Packet>, Byte>> opcodes = new HashMap<>();
  @Getter
  private final byte hashSize;
  @Getter
  private final boolean isAsync;
  @Getter
  private final boolean isCompressed;
  private final Injector injector;

  public Protocol(byte hashSize, boolean isAsync, boolean isCompressed, Injector injector) {
    this.hashSize = hashSize;
    this.isAsync = isAsync;
    this.isCompressed = isCompressed;
    this.injector = injector;
  }

  public void registerPacket(DataFlow flow, byte opcode, Class<? extends Packet> packet) {
    packets.computeIfAbsent(flow, df -> new HashMap<>()).put(opcode, packet);
    opcodes.computeIfAbsent(flow, df -> new HashMap<>()).put(packet, opcode);
  }

  public Packet decode(BufferedPacket msg, DataFlow flow) {
    byte opcode = msg.getOpcode();
    byte[] data = msg.getData();

    if (packets.containsKey(flow) && packets.get(flow).containsKey(opcode)) {
      Class<? extends Packet> packetCls = packets.get(flow).get(opcode);
      if (packetCls == null) {
        throw new IllegalStateException("No packet found for opcode");
      }

      try {
        Packet packet = injector.getInstance(packetCls);
        ByteBufEx buffer = new ByteBufEx(data);
        packet.decode(buffer);
        if (buffer.isReadable()) {
          throw new IllegalStateException("Packet not fully read");
        }

        return packet;
      } catch (Exception e) {
        throw new IllegalStateException("Could not decode packet", e);
      }
    }

    throw new IllegalStateException("Could not find packet for " + flow + " opcode " + opcode);
  }

  public BufferedPacket encode(Packet packet, DataFlow flow) throws Exception {
    Byte opcode = opcodes.computeIfAbsent(flow, df -> new HashMap<>()).get(packet.getClass());
    if (opcode == null) {
      throw new IllegalStateException("No opcode found for packet " + packet.getClass().getSimpleName());
    }

    ByteBufEx buffer = new ByteBufEx(Unpooled.buffer());
    packet.encode(buffer);
    return new BufferedPacket(opcode, Arrays.copyOfRange(buffer.array(), buffer.readerIndex(), buffer.writerIndex()));
  }
}

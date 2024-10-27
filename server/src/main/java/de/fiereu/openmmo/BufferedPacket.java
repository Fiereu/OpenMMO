package de.fiereu.openmmo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BufferedPacket {
  private final byte opcode;
  private final byte[] data;
}

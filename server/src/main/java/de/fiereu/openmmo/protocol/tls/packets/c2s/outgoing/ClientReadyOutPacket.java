package de.fiereu.openmmo.protocol.tls.packets.c2s.outgoing;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.Packet;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.protocol.tls.TlsInfo;
import de.fiereu.openmmo.protocol.tls.TlsProtocol;
import de.fiereu.openmmo.util.ECUtil;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;

public class ClientReadyOutPacket extends OutgoingPacket {
  private final PublicKey connectionKeyPublic;

  public ClientReadyOutPacket(PublicKey connectionKeyPublic) {
    this.connectionKeyPublic = connectionKeyPublic;
  }

  @Override
  public void encode(ByteBufEx buffer) throws Exception {
    assert connectionKeyPublic != null;
    byte[] uncompressedPoint = ECUtil.PublicKeyToUncompressedPoint((ECPublicKey) connectionKeyPublic);
    buffer.writeShortLE(uncompressedPoint.length);
    buffer.writeBytes(uncompressedPoint);
  }
}

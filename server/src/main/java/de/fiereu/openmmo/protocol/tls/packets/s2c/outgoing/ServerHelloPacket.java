package de.fiereu.openmmo.protocol.tls.packets.s2c.outgoing;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.util.ECUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;

public class ServerHelloPacket extends OutgoingPacket {
  private final PrivateKey rootKeyPrivate;
  private final PublicKey connectionKeyPublic;
  private final byte hashSize;

  public ServerHelloPacket(PrivateKey rootKeyPrivate, PublicKey connectionKeyPublic, byte hashSize) {
    this.rootKeyPrivate = rootKeyPrivate;
    this.connectionKeyPublic = connectionKeyPublic;
    this.hashSize = hashSize;
  }

  @Override
  public void encode(ByteBufEx buffer) throws Exception {
    assert connectionKeyPublic instanceof ECPublicKey;
    byte[] uncompressedPoint = ECUtil.PublicKeyToUncompressedPoint((ECPublicKey) connectionKeyPublic);
    buffer.writeShortLE(uncompressedPoint.length);
    buffer.writeBytes(uncompressedPoint);

    byte[] signature = ECUtil.sign(uncompressedPoint, rootKeyPrivate);
    buffer.writeShortLE(signature.length);
    buffer.writeBytes(signature);

    buffer.writeByte(hashSize);
  }
}

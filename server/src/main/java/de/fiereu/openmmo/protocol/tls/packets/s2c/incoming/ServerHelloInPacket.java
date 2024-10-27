package de.fiereu.openmmo.protocol.tls.packets.s2c.incoming;

import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.protocol.tls.TlsInfo;
import de.fiereu.openmmo.protocol.tls.TlsProtocol;
import de.fiereu.openmmo.protocol.tls.packets.c2s.outgoing.ClientReadyOutPacket;
import de.fiereu.openmmo.util.ECUtil;

import java.security.KeyPair;
import java.security.PublicKey;

public class ServerHelloInPacket extends IncomingPacket {
  private byte hashSize;
  private byte[] uncompressedPoint;
  private byte[] signature;

  @Override
  public void decode(ByteBufEx buffer) {
    uncompressedPoint = new byte[buffer.readShortLE()];
    buffer.readBytes(uncompressedPoint);

    signature = new byte[buffer.readShortLE()];
    buffer.readBytes(signature);

    hashSize = buffer.readByte();
  }

  @Override
  public void handle(Session session) throws Exception {
    PublicKey publicKey = ECUtil.PublicKeyFromUncompressedPoint(uncompressedPoint);
    if (!ECUtil.verify(uncompressedPoint, signature, publicKey)) {
      throw new IllegalStateException("Handshake failed due to invalid signature");
    }

    KeyPair keyPair = session.getChannel().attr(TlsProtocol.ATTRIBUTE_KEY_PAIR).get();
    session.send(new ClientReadyOutPacket(keyPair.getPublic()));
    session.enableEncryption(new TlsInfo(keyPair.getPrivate(), publicKey, hashSize));
  }
}

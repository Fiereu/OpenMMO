package de.fiereu.openmmo.protocol.tls.packets.c2s.incoming;

import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.protocol.tls.TlsInfo;
import de.fiereu.openmmo.protocol.tls.TlsProtocol;
import de.fiereu.openmmo.util.ECUtil;

import java.security.KeyPair;
import java.security.PublicKey;

public class ClientReadyPacket extends IncomingPacket {
  private byte[] uncompressedPoint;

  @Override
  public void decode(ByteBufEx buffer) {
    uncompressedPoint = new byte[buffer.readShortLE()];
    buffer.readBytes(uncompressedPoint);
  }

  @Override
  public void handle(Session session) throws Exception {
    PublicKey publicKey = ECUtil.PublicKeyFromUncompressedPoint(uncompressedPoint);
    KeyPair keyPair = session.getChannel().attr(TlsProtocol.ATTRIBUTE_KEY_PAIR).get();
    session.enableEncryption(new TlsInfo(keyPair.getPrivate(), publicKey, session.getEncryptedProtocol().getHashSize()));
  }
}

package de.fiereu.openmmo.protocol.tls.packets.c2s.incoming;

import com.google.inject.Inject;
import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.protocol.tls.TlsProtocol;
import de.fiereu.openmmo.protocol.tls.packets.s2c.outgoing.ServerHelloPacket;
import de.fiereu.openmmo.util.ECUtil;

import java.security.KeyPair;
import java.security.PrivateKey;

public class ClientHelloPacket extends IncomingPacket {
  private static final long key1 = 3214621489648854472L;
  private static final long key2 = -4214651440992349575L;
  @Inject
  private PrivateKey rootPrivateKey;
  private long encryptedRandomValue;
  private long encryptedTimestamp;

  @Override
  public void decode(ByteBufEx buffer) {
    encryptedRandomValue = buffer.readLongLE();
    encryptedTimestamp = buffer.readLongLE();
  }

  @Override
  public void handle(Session session) {
    long randomValue = encryptedRandomValue ^ key1;
    long timestamp = encryptedTimestamp ^ key2 ^ randomValue;

    if (Math.abs(System.currentTimeMillis() - timestamp) > 30000) {
      throw new IllegalStateException("Handshake failed due to invalid timestamp");
    }

    KeyPair keyPair = ECUtil.keyPair();
    session.getChannel().attr(TlsProtocol.ATTRIBUTE_KEY_PAIR).set(keyPair);
    session.send(new ServerHelloPacket(rootPrivateKey, keyPair.getPublic(), session.getEncryptedProtocol().getHashSize()));
  }
}

package de.fiereu.openmmo.protocol.tls;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.fiereu.openmmo.DataFlow;
import de.fiereu.openmmo.Protocol;
import de.fiereu.openmmo.protocol.tls.packets.c2s.incoming.ClientHelloPacket;
import de.fiereu.openmmo.protocol.tls.packets.c2s.incoming.ClientReadyPacket;
import de.fiereu.openmmo.protocol.tls.packets.s2c.outgoing.ServerHelloPacket;
import io.netty.util.AttributeKey;

import java.security.KeyPair;
import java.security.PrivateKey;

public class TlsProtocol extends Protocol {
  public static final AttributeKey<KeyPair> ATTRIBUTE_KEY_PAIR = AttributeKey.valueOf("keyPair");
  public TlsProtocol(RootKeyLoader rootKeyLoader) {
    super((byte) 0, false, false, createInjector(rootKeyLoader));

    registerPacket(DataFlow.CLIENT_TO_SERVER, (byte) 0x00, ClientHelloPacket.class);
    registerPacket(DataFlow.SERVER_TO_CLIENT, (byte) 0x01, ServerHelloPacket.class);
    registerPacket(DataFlow.CLIENT_TO_SERVER, (byte) 0x02, ClientReadyPacket.class);
  }

  private static Injector createInjector(RootKeyLoader rootKeyLoader) {
    return Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(PrivateKey.class).toInstance(rootKeyLoader.getKeyPair().getPrivate());
      }
    });
  }
}

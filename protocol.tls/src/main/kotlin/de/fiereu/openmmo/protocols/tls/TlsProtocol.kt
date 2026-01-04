package de.fiereu.openmmo.protocols.tls

import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.incomingPacket
import de.fiereu.openmmo.protocols.outgoingPacket
import de.fiereu.openmmo.protocols.tls.packets.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

/**
 * TLS Protocol implementation. This protocol handles the initial TLS handshake before any secure
 * communication can take place.
 *
 * The flow is as follows:
 * 1. Client sends a ClientHello packet to initiate the handshake.
 * 2. Server responds with a ServerHello packet. -> The client knows the servers public key and its
 *    own private key. And it can derive a shared secret using ECDH. -> Every packet the client now
 *    would receive is expected to be secured. -> Client switches to next protocol (e.g.
 *    LoginProtocol) after the handshake is complete.
 * 3. Client sends a ClientReady packet with its public key. -> The server can now secure its
 *    communication with the client using the shared secret. -> Server switches to next protocol
 *    (e.g. LoginProtocol) after the handshake is complete.
 */
abstract class TlsProtocol() : Protocol() {
  override val async: Boolean = false
  override val compressed: Boolean = false
}

class TlsServerProtocol(serverKeyPair: Pair<ECPrivateKey, ECPublicKey>) : TlsProtocol() {
  init {
    incomingPacket(0x00u, ClientHelloPacketDeserializer())
    outgoingPacket(0x01u, ServerHelloPacketSerializer(serverKeyPair.first))
    incomingPacket(0x02u, ClientReadyPacketDeserializer())
  }
}

class TlsClientProtocol() : TlsProtocol() {
  init {
    outgoingPacket(0x00u, ClientHelloPacketSerializer())
    incomingPacket(0x01u, ServerHelloPacketDeserializer())
    outgoingPacket(0x02u, ClientReadyPacketSerializer())
  }
}

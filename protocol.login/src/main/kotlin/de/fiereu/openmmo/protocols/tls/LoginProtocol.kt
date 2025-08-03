package de.fiereu.openmmo.protocols.tls

import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.incomingPacket
import de.fiereu.openmmo.protocols.outgoingPacket
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacket
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacketSerializer

/**
 * Login Protocol implementation.
 * This protocol handles the login process after the TLS handshake is complete.
 */
abstract class LoginProtocol() : Protocol() {
  override val async: Boolean
    get() = true
}

class LoginServerProtocol() : LoginProtocol() {
  init {
    incomingPacket(0x11u, LoginRequestPacketDeserializer())
  }
}

class LoginClientProtocol() : LoginProtocol() {
  init {
    outgoingPacket(0x11u, LoginRequestPacket::class, LoginRequestPacketSerializer())
  }
}


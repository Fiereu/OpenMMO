package de.fiereu.openmmo.protocols.tls

import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.incomingPacket
import de.fiereu.openmmo.protocols.outgoingPacket
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacket
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.LoginResponsePacket
import de.fiereu.openmmo.protocols.tls.packets.LoginResponsePacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.LoginResponsePacketSerializer

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
    outgoingPacket(0x01u, LoginResponsePacket::class, LoginResponsePacketSerializer())
    incomingPacket(0x11u, LoginRequestPacketDeserializer())
  }
}

class LoginClientProtocol() : LoginProtocol() {
  init {
    incomingPacket(0x01u, LoginResponsePacketDeserializer())
    outgoingPacket(0x11u, LoginRequestPacket::class, LoginRequestPacketSerializer())
  }
}


package de.fiereu.openmmo.protocols.tls

import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.incomingPacket
import de.fiereu.openmmo.protocols.outgoingPacket
import de.fiereu.openmmo.protocols.tls.packets.GameServerListPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.GameServerListPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.GameServerNodesPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.GameServerNodesPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.JoinGameServerPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.JoinGameServerPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.LoginKickPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.LoginKickPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.LoginResponsePacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.LoginResponsePacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.MfaChallengePacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.MfaChallengePacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.MfaResponsePacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.MfaResponsePacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.RequestGameServerListPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.RequestGameServerListPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.SentCredentialsPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.SentCredentialsPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.SentExistingSessionPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.SentExistingSessionPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.ToSConfirmationPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.ToSConfirmationPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.ToSPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.ToSPacketSerializer

/**
 * Login Protocol implementation. This protocol handles the login process after the TLS handshake is
 * complete.
 */
abstract class LoginProtocol() : Protocol() {
  override val async: Boolean = true
  override val compressed: Boolean = false
}

class LoginServerProtocol() : LoginProtocol() {
  init {
    outgoingPacket(0x01u, LoginResponsePacketSerializer())
    incomingPacket(0x02u, RequestGameServerListPacketDeserializer())
    incomingPacket(0x03u, JoinGameServerPacketDeserializer())
    outgoingPacket(0x03u, GameServerNodesPacketSerializer())
    incomingPacket(0x04u, ToSConfirmationPacketDeserializer())
    outgoingPacket(0x05u, LoginKickPacketSerializer())
    outgoingPacket(0x07u, SentCredentialsPacketSerializer())
    outgoingPacket(0x08u, MfaChallengePacketSerializer())
    incomingPacket(0x08u, MfaResponsePacketDeserializer())
    incomingPacket(0x11u, LoginRequestPacketDeserializer())
    outgoingPacket(0x14u, ToSPacketSerializer())
    outgoingPacket(0x22u, GameServerListPacketSerializer())
    outgoingPacket(0x26u, SentExistingSessionPacketSerializer())
  }
}

class LoginClientProtocol() : LoginProtocol() {
  init {
    incomingPacket(0x01u, LoginResponsePacketDeserializer())
    outgoingPacket(0x02u, RequestGameServerListPacketSerializer())
    outgoingPacket(0x03u, JoinGameServerPacketSerializer())
    incomingPacket(0x03u, GameServerNodesPacketDeserializer())
    outgoingPacket(0x04u, ToSConfirmationPacketSerializer())
    incomingPacket(0x05u, LoginKickPacketDeserializer())
    incomingPacket(0x07u, SentCredentialsPacketDeserializer())
    incomingPacket(0x08u, MfaChallengePacketDeserializer())
    outgoingPacket(0x08u, MfaResponsePacketSerializer())
    outgoingPacket(0x11u, LoginRequestPacketSerializer())
    incomingPacket(0x14u, ToSPacketDeserializer())
    incomingPacket(0x22u, GameServerListPacketDeserializer())
    incomingPacket(0x26u, SentExistingSessionPacketDeserializer())
  }
}

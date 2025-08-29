package de.fiereu.openmmo.protocols.tls

import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.incomingPacket
import de.fiereu.openmmo.protocols.outgoingPacket
import de.fiereu.openmmo.protocols.tls.packets.GameServerNodesPacket
import de.fiereu.openmmo.protocols.tls.packets.GameServerNodesPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.GameServerNodesPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.JoinGameServerPacket
import de.fiereu.openmmo.protocols.tls.packets.JoinGameServerPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.JoinGameServerPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacket
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.LoginResponsePacket
import de.fiereu.openmmo.protocols.tls.packets.LoginResponsePacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.LoginResponsePacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.RequestGameServerListPacket
import de.fiereu.openmmo.protocols.tls.packets.RequestGameServerListPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.RequestGameServerListPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.GameServerListPacket
import de.fiereu.openmmo.protocols.tls.packets.GameServerListPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.GameServerListPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.ToSConfirmationPacket
import de.fiereu.openmmo.protocols.tls.packets.ToSConfirmationPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.ToSConfirmationPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.LoginKickPacket
import de.fiereu.openmmo.protocols.tls.packets.LoginKickPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.LoginKickPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.SentCredentialsPacket
import de.fiereu.openmmo.protocols.tls.packets.SentCredentialsPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.SentCredentialsPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.MfaChallengePacket
import de.fiereu.openmmo.protocols.tls.packets.MfaChallengePacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.MfaChallengePacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.MfaResponsePacket
import de.fiereu.openmmo.protocols.tls.packets.MfaResponsePacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.MfaResponsePacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.ToSPacket
import de.fiereu.openmmo.protocols.tls.packets.ToSPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.ToSPacketSerializer
import de.fiereu.openmmo.protocols.tls.packets.ExistingSessionPacket
import de.fiereu.openmmo.protocols.tls.packets.SentExistingSessionPacketDeserializer
import de.fiereu.openmmo.protocols.tls.packets.SentExistingSessionPacketSerializer

/**
 * Login Protocol implementation. This protocol handles the login process after the TLS handshake is
 * complete.
 */
abstract class LoginProtocol() : Protocol() {
  override val async: Boolean
    get() = true
}

class LoginServerProtocol() : LoginProtocol() {
  init {
    outgoingPacket(0x01u, LoginResponsePacketSerializer(), LoginResponsePacket::class)
    incomingPacket(0x02u, RequestGameServerListPacketDeserializer())
    incomingPacket(0x03u, JoinGameServerPacketDeserializer())
    outgoingPacket(0x03u, GameServerNodesPacketSerializer(), GameServerNodesPacket::class)
    incomingPacket(0x04u, ToSConfirmationPacketDeserializer())
    outgoingPacket(0x05u, LoginKickPacketSerializer(), LoginKickPacket::class)
    outgoingPacket(0x07u, SentCredentialsPacketSerializer(), SentCredentialsPacket::class)
    outgoingPacket(0x08u, MfaChallengePacketSerializer(), MfaChallengePacket::class)
    incomingPacket(0x08u, MfaResponsePacketDeserializer())
    incomingPacket(0x11u, LoginRequestPacketDeserializer())
    outgoingPacket(0x14u, ToSPacketSerializer(), ToSPacket::class)
    outgoingPacket(0x22u, GameServerListPacketSerializer(), GameServerListPacket::class)
    outgoingPacket(0x26u, SentExistingSessionPacketSerializer(), ExistingSessionPacket::class)
  }
}

class LoginClientProtocol() : LoginProtocol() {
  init {
    incomingPacket(0x01u, LoginResponsePacketDeserializer())
    outgoingPacket(0x02u, RequestGameServerListPacketSerializer(), RequestGameServerListPacket::class)
    outgoingPacket(0x03u, JoinGameServerPacketSerializer(), JoinGameServerPacket::class)
    incomingPacket(0x03u, GameServerNodesPacketDeserializer())
    outgoingPacket(0x04u, ToSConfirmationPacketSerializer(), ToSConfirmationPacket::class)
    incomingPacket(0x05u, LoginKickPacketDeserializer())
    incomingPacket(0x07u, SentCredentialsPacketDeserializer())
    incomingPacket(0x08u, MfaChallengePacketDeserializer())
    outgoingPacket(0x08u, MfaResponsePacketSerializer(), MfaResponsePacket::class)
    outgoingPacket(0x11u, LoginRequestPacketSerializer(), LoginRequestPacket::class)
    incomingPacket(0x14u, ToSPacketDeserializer())
    incomingPacket(0x22u, GameServerListPacketDeserializer())
    incomingPacket(0x26u, SentExistingSessionPacketDeserializer())
  }
}

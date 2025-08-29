package de.fiereu.openmmo.protocols.game

import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.game.packets.JoinGamePacket
import de.fiereu.openmmo.protocols.game.packets.JoinPacketDeserializer
import de.fiereu.openmmo.protocols.game.packets.JoinPacketSerializer
import de.fiereu.openmmo.protocols.incomingPacket
import de.fiereu.openmmo.protocols.outgoingPacket

/**
 * Game Protocol implementation. This protocol handles in-game communication after the TLS handshake
 * is complete.
 */
abstract class GameProtocol : Protocol() {
  override val async: Boolean
    get() = true
}

class GameServerProtocol : GameProtocol() {
  init {
    incomingPacket(0x01u, JoinPacketDeserializer())
  }
}

class GameClientProtocol : GameProtocol() {
  init {
    outgoingPacket(0x01u, JoinPacketSerializer(), JoinGamePacket::class)
  }
}

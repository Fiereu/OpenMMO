package de.fiereu.openmmo.server.login.protocol.login.ext

import de.fiereu.openmmo.common.enums.LoginState
import de.fiereu.openmmo.protocols.tls.packets.JoinGameServerPacket
import de.fiereu.openmmo.protocols.tls.packets.builders.AuthedPacketBuilder
import de.fiereu.openmmo.protocols.tls.packets.builders.GameServerNodesPacketBuilder
import de.fiereu.openmmo.server.protocol.PacketEvent
import de.fiereu.openmmo.server.protocol.respond

fun PacketEvent<JoinGameServerPacket>.respondWithErrorState(state: LoginState) {
  this.respond(GameServerNodesPacketBuilder.forState(state))
}

fun PacketEvent<JoinGameServerPacket>.respondForAuthedUser(
  userId: Int,
  sessionToken: ByteArray
): AuthedPacketBuilder {
  return GameServerNodesPacketBuilder.forAuthedUser(
    userId = userId,
    sessionToken = sessionToken,
    gameServerId = this.packet.gameServerId
  )
}

fun AuthedPacketBuilder.respondTo(event: PacketEvent<*>) {
  event.respond(this.build())
}
package de.fiereu.openmmo.server.login.protocol.login.ext

import de.fiereu.openmmo.common.enums.LoginState
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacket
import de.fiereu.openmmo.protocols.tls.packets.builders.LoginResponsePacketBuilder
import de.fiereu.openmmo.server.protocol.PacketEvent
import de.fiereu.openmmo.server.protocol.respond
import java.time.LocalDateTime

fun PacketEvent<LoginRequestPacket>.respondWithState(state: LoginState) {
  this.respond(LoginResponsePacketBuilder.withState(state))
}

fun PacketEvent<LoginRequestPacket>.respondWithRateLimit(
    state: LoginState,
    ratelimitEnd: LocalDateTime
) {
  this.respond(LoginResponsePacketBuilder.withRateLimit(state, ratelimitEnd))
}

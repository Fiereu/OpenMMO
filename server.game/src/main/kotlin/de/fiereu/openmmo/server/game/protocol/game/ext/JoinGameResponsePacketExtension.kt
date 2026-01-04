package de.fiereu.openmmo.server.game.protocol.game.ext

import de.fiereu.openmmo.protocols.game.packets.JoinGamePacket
import de.fiereu.openmmo.protocols.game.packets.builders.JoinResponsePacketBuilder
import de.fiereu.openmmo.server.protocol.PacketEvent
import de.fiereu.openmmo.server.protocol.respond

fun PacketEvent<JoinGamePacket>.reject() {
  this.respond(JoinResponsePacketBuilder.reject())
}

fun PacketEvent<JoinGamePacket>.accept() = JoinResponsePacketBuilder.accept()

fun JoinResponsePacketBuilder.buildAndRespond(event: PacketEvent<JoinGamePacket>) {
  event.respond(this.build())
}

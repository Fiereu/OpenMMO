package de.fiereu.openmmo.server.login.protocol.login.ext

import de.fiereu.openmmo.protocols.tls.packets.GameServer
import de.fiereu.openmmo.protocols.tls.packets.RequestGameServerListPacket
import de.fiereu.openmmo.protocols.tls.packets.builders.GameServerListPacketBuilder
import de.fiereu.openmmo.server.protocol.PacketEvent
import de.fiereu.openmmo.server.protocol.respond

fun PacketEvent<RequestGameServerListPacket>.respondWithServers(servers: List<GameServer>) {
  this.respond(GameServerListPacketBuilder.withServers(servers))
}

fun PacketEvent<RequestGameServerListPacket>.respondWithEmptyList() {
  this.respond(GameServerListPacketBuilder.empty())
}

fun PacketEvent<RequestGameServerListPacket>.respondWithServerList(): GameServerListPacketBuilder {
  return GameServerListPacketBuilder.create()
}

fun GameServerListPacketBuilder.respondTo(event: PacketEvent<*>) {
  event.respond(this.build())
}

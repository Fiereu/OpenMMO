package de.fiereu.openmmo.protocols.tls.packets.builders

import de.fiereu.openmmo.protocols.tls.packets.GameServer
import de.fiereu.openmmo.protocols.tls.packets.GameServerListPacket

class GameServerListPacketBuilder {
  private val servers = mutableListOf<GameServer>()
  
  companion object {
    fun create(): GameServerListPacketBuilder = GameServerListPacketBuilder()
    
    fun withServers(servers: List<GameServer>): GameServerListPacket {
      return GameServerListPacket(servers)
    }
    
    fun empty(): GameServerListPacket = GameServerListPacket(emptyList())
  }
  
  fun addServer(
    id: UByte,
    name: String,
    currentPlayers: UShort = 0u,
    maxPlayers: UShort = 0u,
    joinable: Boolean = true
  ): GameServerListPacketBuilder {
    servers.add(GameServer(id, name, currentPlayers, maxPlayers, joinable))
    return this
  }
  
  fun addServer(server: GameServer): GameServerListPacketBuilder {
    servers.add(server)
    return this
  }
  
  fun build(): GameServerListPacket = GameServerListPacket(servers.toList())
}
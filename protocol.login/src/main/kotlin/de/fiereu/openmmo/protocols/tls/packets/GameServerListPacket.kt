package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

data class GameServer(
    val id: UByte,
    val name: String,
    val currentPlayers: UShort = 0u,
    val maxPlayers: UShort = 0u,
    val joinable: Boolean
)

data class GameServerListPacket(val gameServers: List<GameServer>)

class GameServerListPacketSerializer : PacketSerializer<GameServerListPacket> {
  override fun serialize(packet: GameServerListPacket, buffer: ByteBuf) {
    require(packet.gameServers.size <= Byte.MAX_VALUE) { "Too many game servers" }

    buffer.writeByte(packet.gameServers.size)

    if (packet.gameServers.isEmpty()) {
      buffer.writeByte(0)
      buffer.writeByte(0)
      return
    }

    val first = packet.gameServers.first()
    buffer.writeByte(first.id.toInt())

    for (gameServer in packet.gameServers) {
      buffer.writeByte(gameServer.id.toInt())
      buffer.writeUtf16LE(gameServer.name)
      buffer.writeShortLE(gameServer.currentPlayers.toInt())
      buffer.writeShortLE(gameServer.maxPlayers.toInt())
      buffer.writeBoolean(gameServer.joinable)
    }
  }
}

class GameServerListPacketDeserializer : PacketDeserializer<GameServerListPacket> {
  override fun deserialize(buffer: ByteBuf): GameServerListPacket {
    val serverCount = buffer.readUnsignedByte().toInt()

    if (serverCount == 0) {
      buffer.readByte() // skip first server id (0)
      buffer.readByte() // skip second byte (0)
      return GameServerListPacket(emptyList())
    }

    val firstServerId = buffer.readUnsignedByte().toUByte()
    val gameServers = mutableListOf<GameServer>()

    for (i in 0 until serverCount) {
      val id = buffer.readUnsignedByte().toUByte()
      val name = buffer.readUtf16LE()
      val currentPlayers = buffer.readUnsignedShortLE().toUShort()
      val maxPlayers = buffer.readUnsignedShortLE().toUShort()
      val joinable = buffer.readBoolean()

      gameServers.add(GameServer(id, name, currentPlayers, maxPlayers, joinable))
    }

    return GameServerListPacket(gameServers)
  }
}

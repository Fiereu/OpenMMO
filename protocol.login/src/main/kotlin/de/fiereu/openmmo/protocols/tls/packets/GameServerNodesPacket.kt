package de.fiereu.openmmo.protocols.tls.packets

import com.github.maltalex.ineter.base.IPAddress
import com.github.maltalex.ineter.base.IPv4Address
import com.github.maltalex.ineter.base.IPv6Address
import de.fiereu.openmmo.common.enums.LoginState
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readIpLE
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeIpLE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

/**
 * Nodes are address entries for game servers that the client can connect to. The nodes are selected
 * by a weighted random algorithm on the client side. The weight is a value between 0 and 255, where
 * 0 means the node will never be selected and 255 means the node will always be selected.
 * Typically, nodes are assigned weights based on their capacity or priority.
 */
data class GameServerNode(
    val iPv4Address: IPv4Address,
    val iPv6Address: IPv6Address,
    val port: UShort = 7777u,
    val weight: UByte
)

data class GameServerData(
    val gameServerId: UByte,
    val userId: Int,
    val sessionToken: ByteArray,
    val localAddress: IPAddress,
    val localHostname: String,
    val port: UShort,
)

data class GameServerNodesPacket(
    val loginState: LoginState,
    val gameServerData: GameServerData? = null,
    val nodes: List<GameServerNode> = emptyList()
)

class GameServerNodesPacketSerializer : PacketSerializer<GameServerNodesPacket> {
  override fun serialize(packet: GameServerNodesPacket, buffer: ByteBuf) {
    buffer.writeByte(packet.loginState.id)
    if (packet.loginState != LoginState.AUTHED) {
      return
    }

    val gsd =
        requireNotNull(packet.gameServerData) { "GameServerData must be provided for AUTHED state" }

    buffer.writeIntLE(gsd.userId)
    buffer.writeByte(gsd.sessionToken.size)
    buffer.writeBytes(gsd.sessionToken)

    buffer.writeByte(gsd.gameServerId.toInt())

    val localAddressBytes = gsd.localAddress.toBigEndianArray()
    buffer.writeByte(localAddressBytes.size)
    buffer.writeBytes(localAddressBytes)
    buffer.writeUtf16LE(gsd.localHostname)

    buffer.writeIntLE(gsd.port.toInt())

    buffer.writeByte(packet.nodes.size)
    for ((index, node) in packet.nodes.withIndex()) {
      buffer.writeByte(index)

      buffer.writeIpLE(node.iPv4Address)
      buffer.writeIpLE(node.iPv6Address)

      buffer.writeShortLE(node.port.toInt())
      buffer.writeByte(node.weight.toInt())
    }
  }
}

class GameServerNodesPacketDeserializer : PacketDeserializer<GameServerNodesPacket> {

  override fun deserialize(buffer: ByteBuf): GameServerNodesPacket {
    val stateId = buffer.readUnsignedByte().toInt()
    val loginState = LoginState.entries.find { it.id == stateId } ?: LoginState.SYSTEM_ERROR
    if (loginState != LoginState.AUTHED) {
      return GameServerNodesPacket(loginState)
    }

    val userId = buffer.readIntLE()

    val sessionTokenSize = buffer.readUnsignedByte().toInt()
    val sessionToken = ByteArray(sessionTokenSize)
    buffer.readBytes(sessionToken)

    val gameServerId = buffer.readUnsignedByte().toUByte()

    val localAddressBuffer = ByteArray(buffer.readUnsignedByte().toInt())
    buffer.readBytes(localAddressBuffer)
    val localAddress = IPAddress.of(localAddressBuffer)
    val localHostname = buffer.readUtf16LE()

    val port = buffer.readUnsignedIntLE().toUShort()

    val nodeCount = buffer.readUnsignedByte().toInt()
    val nodes = mutableListOf<GameServerNode>()
    for (i in 0 until nodeCount) {
      buffer.readByte() // padding

      val iPv4Address = buffer.readIpLE() as IPv4Address
      val iPv6Address = buffer.readIpLE() as IPv6Address

      val nodePort = buffer.readUnsignedShortLE().toUShort()
      val weight = buffer.readUnsignedByte().toUByte()

      nodes.add(GameServerNode(iPv4Address, iPv6Address, nodePort, weight))
    }

    return GameServerNodesPacket(
        LoginState.AUTHED,
        GameServerData(gameServerId, userId, sessionToken, localAddress, localHostname, port),
        nodes)
  }
}

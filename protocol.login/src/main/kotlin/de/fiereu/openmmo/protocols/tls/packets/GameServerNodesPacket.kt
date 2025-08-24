package de.fiereu.openmmo.protocols.tls.packets

import com.github.maltalex.ineter.base.IPAddress
import com.github.maltalex.ineter.base.IPv4Address
import com.github.maltalex.ineter.base.IPv6Address
import de.fiereu.openmmo.enums.LoginState
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readIpLE
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeIpLE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

data class GameServerNode(
  val id: UByte,
  val iPv4Address: IPv4Address,
  val iPv6Address: IPv6Address,
  val port: UShort
)

data class GameServerNodesPacket(
  val loginState: LoginState,
  val gameServerId: UByte,
  val userId: Int,
  val sessionToken: ByteArray,
  val localAddress: IPAddress, // is 127.0.0.1 when connection to official login server
  val localHostname: String, // is localhost when connection to official login server
  val port: UShort,
  val nodes: List<GameServerNode>
)

class GameServerNodesPacketSerializer : PacketSerializer<GameServerNodesPacket> {
  override fun serialize(packet: GameServerNodesPacket, buffer: ByteBuf) {
    buffer.writeByte(packet.loginState.id)
    if (packet.loginState != LoginState.AUTHED) {
      return
    }

    buffer.writeIntLE(packet.userId)
    buffer.writeByte(packet.sessionToken.size)
    buffer.writeBytes(packet.sessionToken)

    buffer.writeByte(packet.gameServerId.toInt())

    val localAddressBytes = packet.localAddress.toLittleEndianArray()
    buffer.writeByte(localAddressBytes.size)
    buffer.writeBytes(localAddressBytes)
    buffer.writeUtf16LE(packet.localHostname)

    buffer.writeShortLE(packet.port.toInt())

    buffer.writeByte(packet.nodes.size)
    for (node in packet.nodes) {
      buffer.writeByte(0) // padding

      buffer.writeIpLE(node.iPv4Address)
      buffer.writeIpLE(node.iPv6Address)

      buffer.writeShortLE(node.port.toInt())
      buffer.writeByte(node.id.toInt())
    }
  }
}

class GameServerNodesPacketDeserializer : PacketDeserializer<GameServerNodesPacketDeserializer.GameServerNodesPacket> {

  open class GameServerNodesPacket(
    open val loginState: LoginState
  )

  data class AuthedGameServerNodesPacket(
    override val loginState: LoginState,
    val gameServerId: UByte,
    val userId: Int,
    val sessionToken: ByteArray,
    val localAddress: IPAddress,
    val localHostname: String,
    val port: UShort,
    val nodes: List<GameServerNode>
  ) : GameServerNodesPacket(loginState)

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

    val localAddress = buffer.readIpLE()
    val localHostname = buffer.readUtf16LE()

    val port = buffer.readUnsignedShortLE().toUShort()

    val nodeCount = buffer.readUnsignedByte().toInt()
    val nodes = mutableListOf<GameServerNode>()
    for (i in 0 until nodeCount) {
      buffer.readByte() // padding

      val iPv4Address = buffer.readIpLE() as IPv4Address
      val iPv6Address = buffer.readIpLE() as IPv6Address

      val nodePort = buffer.readUnsignedShortLE().toUShort()
      val nodeId = buffer.readUnsignedByte().toUByte()

      nodes.add(GameServerNode(nodeId, iPv4Address, iPv6Address, nodePort))
    }

    return AuthedGameServerNodesPacket(
      loginState,
      gameServerId,
      userId,
      sessionToken,
      localAddress,
      localHostname,
      port,
      nodes
    )
  }
}


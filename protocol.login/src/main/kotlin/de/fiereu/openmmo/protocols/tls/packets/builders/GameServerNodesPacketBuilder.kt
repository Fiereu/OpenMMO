package de.fiereu.openmmo.protocols.tls.packets.builders

import com.github.maltalex.ineter.base.IPAddress
import de.fiereu.openmmo.common.enums.LoginState
import de.fiereu.openmmo.protocols.tls.packets.GameServerData
import de.fiereu.openmmo.protocols.tls.packets.GameServerNode
import de.fiereu.openmmo.protocols.tls.packets.GameServerNodesPacket

class GameServerNodesPacketBuilder {
  companion object {
    fun forState(loginState: LoginState): GameServerNodesPacket {
      require(loginState != LoginState.AUTHED) { "Use forAuthedUser() for AUTHED state" }
      return GameServerNodesPacket(loginState)
    }

    fun forAuthedUser(
        userId: Int,
        sessionToken: ByteArray,
        gameServerId: UByte
    ): AuthedPacketBuilder {
      return AuthedPacketBuilder(userId, sessionToken, gameServerId)
    }
  }
}

class AuthedPacketBuilder
internal constructor(
    private val userId: Int,
    private val sessionToken: ByteArray,
    private val gameServerId: UByte
) {
  private var localAddress: IPAddress? = null
  private var localHostname: String? = null
  private var port: UShort? = null
  private var nodes: List<GameServerNode> = emptyList()

  fun withLocalEndpoint(address: IPAddress, hostname: String, port: UShort): AuthedPacketBuilder {
    this.localAddress = address
    this.localHostname = hostname
    this.port = port
    return this
  }

  fun withNodes(nodes: List<GameServerNode>): AuthedPacketBuilder {
    this.nodes = nodes
    return this
  }

  fun build(): GameServerNodesPacket {
    requireNotNull(localAddress) { "Local address must be set for AUTHED packets" }
    requireNotNull(localHostname) { "Local hostname must be set for AUTHED packets" }
    requireNotNull(port) { "Port must be set for AUTHED packets" }

    val gameServerData =
        GameServerData(gameServerId, userId, sessionToken, localAddress!!, localHostname!!, port!!)

    return GameServerNodesPacket(LoginState.AUTHED, gameServerData, nodes)
  }
}

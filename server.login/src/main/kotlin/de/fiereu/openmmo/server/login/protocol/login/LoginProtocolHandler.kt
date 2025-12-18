package de.fiereu.openmmo.server.login.protocol.login

import com.github.maltalex.ineter.base.IPAddress
import com.github.maltalex.ineter.base.IPv4Address
import com.github.maltalex.ineter.base.IPv6Address
import de.fiereu.openmmo.common.enums.LoginState
import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.tls.packets.*
import de.fiereu.openmmo.server.login.protocol.login.ext.respondForAuthedUser
import de.fiereu.openmmo.server.login.protocol.login.ext.respondTo
import de.fiereu.openmmo.server.login.protocol.login.ext.respondWithServers
import de.fiereu.openmmo.server.login.protocol.login.ext.respondWithState
import de.fiereu.openmmo.server.login.services.UserAuthenticationService
import de.fiereu.openmmo.server.netty.handlers.ProtocolHandler
import de.fiereu.openmmo.server.protocol.PacketEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LoginProtocolHandler(
  protocol: Protocol,
  private val coroutineScope: CoroutineScope,
  private val userAuthenticationService: UserAuthenticationService
) : ProtocolHandler(protocol) {

  private val log = KotlinLogging.logger {}

  override fun onActive(ctx: ChannelHandlerContext) {
    log.info { "Client ${ctx.channel().remoteAddress()} swapped to login protocol." }
  }

  @Suppress("unchecked_cast") // Every cast is personally checked and signed off by me AT RUNTIME :P
  override fun onPacketReceived(event: PacketEvent<*>) {
    coroutineScope.launch {
      when (event.packet) {
        is LoginRequestPacket -> onLoginRequest(event as PacketEvent<LoginRequestPacket>)
        is RequestGameServerListPacket ->
          onGameServerListRequest(event as PacketEvent<RequestGameServerListPacket>)

        is JoinGameServerPacket -> onServerSelection(event as PacketEvent<JoinGameServerPacket>)
        else -> log.warn { "Unhandled login packet type: ${event.packet::class.simpleName}" }
      }
    }
  }

  fun onLoginRequest(event: PacketEvent<LoginRequestPacket>) {
    log.info { "Received login request for user '${event.packet.username}'" }
    when (event.packet.method) {
      is LoginMethod.Password -> {
        val loginMethod = event.packet.method as LoginMethod.Password
        if (userAuthenticationService.loginPassword(event.packet.username, loginMethod.password)) {
          event.respondWithState(LoginState.AUTHED)
        } else {
          event.respondWithState(LoginState.INVALID_PASSWORD)
        }
        return
      }
      is LoginMethod.Token -> TODO()
    }
  }

  fun onGameServerListRequest(event: PacketEvent<RequestGameServerListPacket>) {
    log.info { "Received game server list request" }
    // TODO replace hardcoded server list with actual server list from database or config
    event.respondWithServers(listOf(GameServer(0x00u, "OpenMMO", 0u, 1u, true)))
  }

  fun onServerSelection(event: PacketEvent<JoinGameServerPacket>) {
    log.info { "Received server selection for server id ${event.packet.gameServerId}" }
    // TODO proper server node selection handling (depends on onGameServerListRequest
    // implementation)
    // Example refuse connection: event.respondWithState(LoginState.INVALID_PASSWORD)
    event
      .respondForAuthedUser(userId = 1, sessionToken = ByteArray(16) { 0x42 })
      .withLocalEndpoint(
        address = IPAddress.of("127.0.0.1"), hostname = "localhost", port = 7777u
      )
      .withNodes(
        listOf(
          GameServerNode(
            iPv4Address = IPv4Address.of("127.0.0.1"),
            iPv6Address = IPv6Address.of("::1"),
            port = 7777u,
            weight =
              0x01u // Always select this node (only one node available in this example)
          )
        )
      )
      .respondTo(event)
  }
}

package de.fiereu.openmmo.server.login.protocol.login

import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.tls.packets.LoginRequestPacket
import de.fiereu.openmmo.server.netty.handlers.ProtocolHandler
import de.fiereu.openmmo.server.protocol.PacketEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val log = KotlinLogging.logger {}

class LoginProtocolHandler(
    protocol: Protocol,
    private val coroutineScope: CoroutineScope,
) : ProtocolHandler(protocol) {

  override fun onActive(ctx: ChannelHandlerContext) {
    log.info { "Client ${ctx.channel().remoteAddress()} swapped to login protocol." }
  }

  @Suppress("unchecked_cast") // Every cast is personally checked and signed off by me AT RUNTIME :P
  override fun onPacketReceived(event: PacketEvent<*>) {
    coroutineScope.launch {
      when (event.packet) {
        is LoginRequestPacket -> onLoginRequest(event as PacketEvent<LoginRequestPacket>)
        else -> log.warn { "Unhandled login packet type: ${event.packet::class.simpleName}" }
      }
    }
  }

  fun onLoginRequest(event: PacketEvent<LoginRequestPacket>) {
    log.info { "Received login request for user '${event.packet.username}'" }
  }
}

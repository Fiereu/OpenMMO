package de.fiereu.openmmo.server.game.protocol.game

import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.game.packets.JoinGamePacket
import de.fiereu.openmmo.server.netty.handlers.ProtocolHandler
import de.fiereu.openmmo.server.protocol.PacketEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val log = KotlinLogging.logger {}

class GameProtocolHandler(
    protocol: Protocol,
    private val coroutineScope: CoroutineScope,
) : ProtocolHandler(protocol) {

  override fun onActive(ctx: ChannelHandlerContext) {
    log.info { "Client ${ctx.channel().remoteAddress()} connected to game server." }
  }

  @Suppress("unchecked_cast") // Even here i personally check every cast. i live in ur jvm >:(
  override fun onPacketReceived(event: PacketEvent<*>) {
    coroutineScope.launch {
      when (event.packet) {
        is JoinGamePacket -> onJoinGame(event as PacketEvent<JoinGamePacket>)
        else -> log.warn { "Unhandled game packet type: ${event.packet::class.simpleName}" }
      }
    }
  }

  fun onJoinGame(event: PacketEvent<JoinGamePacket>) {
    log.info { "Player joined the game." }
    log.debug {
      "This is what the Game knows about you:\n${event.packet.clientInfo.values.joinToString("\n")}"
    }
    // TODO handle player joining the game
  }
}

package de.fiereu.openmmo.server.protocol

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelHandlerContext

val log = KotlinLogging.logger {}

data class PacketEvent<T : Any>(
    val ctx: ChannelHandlerContext,
    val packet: T
)

fun <T : Any> PacketEvent<T>.disconnect(reason: () -> String = { "No reason provided" }) {
    log.info { "Stopping server for client ${ctx.channel().remoteAddress()}: ${reason()}" }
    ctx.close()
}

fun <T : Any> PacketEvent<T>.respond(packet: Any) {
    ctx.channel().writeAndFlush(packet) // need to write to channel otherwise the ProtocolHandler will be skipped
}
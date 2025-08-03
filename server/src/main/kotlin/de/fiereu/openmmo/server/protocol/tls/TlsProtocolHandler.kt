package de.fiereu.openmmo.server.protocol.tls

import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.tls.packets.ClientHelloPacket
import de.fiereu.openmmo.protocols.tls.packets.ClientReadyPacket
import de.fiereu.openmmo.protocols.tls.packets.ServerHelloPacket
import de.fiereu.openmmo.server.config.TlsConfig
import de.fiereu.openmmo.server.netty.DefaultChannelHandlerProvider
import de.fiereu.openmmo.server.netty.handlers.ProtocolHandler
import de.fiereu.openmmo.server.protocol.PacketEvent
import de.fiereu.openmmo.server.protocol.disconnect
import de.fiereu.openmmo.server.protocol.respond
import de.fiereu.openmmo.server.protocol.tls.checksum.ChecksumFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec

private val log = KotlinLogging.logger {}

class TlsProtocolHandler(
  protocol: Protocol,
  private val tlsConfig: TlsConfig,
  private val channelHandlerProvider: DefaultChannelHandlerProvider
) : ProtocolHandler(protocol) {

  private val packetBuffer: MutableList<ByteBuf> = mutableListOf()
  private val connectionKeyPair: Pair<ECPublicKey, ECPrivateKey>

  init {
    val keyPairGenerator = KeyPairGenerator.getInstance("EC")
    keyPairGenerator.initialize(ECGenParameterSpec("secp256r1"))
    keyPairGenerator.generateKeyPair().let {
      connectionKeyPair = Pair(it.public as ECPublicKey, it.private as ECPrivateKey)
    }
  }

  @Suppress("unchecked_cast") // The cast is in fact checked :D
  override fun onPacketReceived(event: PacketEvent<*>) {
    when (val packet = event.packet) {
      is ClientHelloPacket -> onClientHello(event as PacketEvent<ClientHelloPacket>)
      is ClientReadyPacket -> onClientReady(event as PacketEvent<ClientReadyPacket>)
      else -> log.warn { "Unhandled TLS packet type: ${packet::class.simpleName}" }
    }
  }

  override fun onActive(ctx: ChannelHandlerContext) {
    log.info { "New client connected: ${ctx.channel().remoteAddress()}" }
  }

  fun onClientHello(event: PacketEvent<ClientHelloPacket>) {
    if (System.currentTimeMillis() - event.packet.timestamp > 10_000) { // 10 seconds is arbitrary
      event.disconnect { "Received stale ClientHello packet" }
      return
    }

    event.respond(ServerHelloPacket(connectionKeyPair.first, tlsConfig.checksumSize))
  }

  fun onClientReady(event: PacketEvent<ClientReadyPacket>) {
    val tlsContext = DefaultTlsContext(
      connectionKeyPair.second,
      event.packet.clientPublicKey
    )
    val checksum = ChecksumFactory.create(tlsConfig.checksumSize, tlsContext.getClientSeed())

    channelHandlerProvider.switchChecksum(event.ctx, checksum)
    channelHandlerProvider.switchTlsContext(event.ctx, tlsContext)
    channelHandlerProvider.enableProtocolHandler(event.ctx)
    log.info { "Client ${event.ctx.channel().remoteAddress()} completed TLS handshake. Switching to next protocol." }
  }
}
package de.fiereu.openmmo.server.netty

import de.fiereu.openmmo.protocols.tls.TlsServerProtocol
import de.fiereu.openmmo.server.config.ServerConfig
import de.fiereu.openmmo.server.config.TlsConfig
import de.fiereu.openmmo.server.netty.handlers.ProtocolHandler
import de.fiereu.openmmo.server.netty.handlers.checksum.ChecksumFrameDecoder
import de.fiereu.openmmo.server.netty.handlers.checksum.ChecksumFrameEncoder
import de.fiereu.openmmo.server.netty.handlers.frame.PacketFrameDecoder
import de.fiereu.openmmo.server.netty.handlers.frame.PacketFrameEncoder
import de.fiereu.openmmo.server.netty.handlers.tls.TlsDecryptionHandler
import de.fiereu.openmmo.server.netty.handlers.tls.TlsEncryptionHandler
import de.fiereu.openmmo.server.protocol.tls.NoOpTlsContext
import de.fiereu.openmmo.server.protocol.tls.TlsContext
import de.fiereu.openmmo.server.protocol.tls.TlsProtocolHandler
import de.fiereu.openmmo.server.protocol.tls.checksum.Checksum
import de.fiereu.openmmo.server.protocol.tls.checksum.NoOpChecksum
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPipeline
import io.netty.handler.timeout.WriteTimeoutHandler
import java.util.concurrent.TimeUnit

/**
 * Base class for configuring Netty channel handlers in the server.
 * Default handlers:
 *  - Timeout handler (Timeout defined in [ServerConfig])
 *  - Packet frame codec
 *  - Checksum frame codec (initially NoOp, can be switched dynamically)
 *  - TLS encryption/decryption handlers (initially NoOp, can be switched dynamically)
 */
class DefaultChannelHandlerProvider(
  private val serverConfig: ServerConfig,
  private val tlsConfig: TlsConfig,
  private val tlsProtocol: TlsServerProtocol,
  private val protocolProvider: () -> ProtocolHandler,
) : ChannelHandlerProvider {

  override fun configurePipeline(pipeline: ChannelPipeline) {
    pipeline.addLast("timeout", WriteTimeoutHandler(serverConfig.writeTimeoutSeconds, TimeUnit.SECONDS))

    pipeline.addLast("frame-decoder", PacketFrameDecoder())
    pipeline.addLast("frame-encoder", PacketFrameEncoder())

    pipeline.addLast("checksum-decoder", ChecksumFrameDecoder(NoOpChecksum()))
    pipeline.addLast("checksum-encoder", ChecksumFrameEncoder(NoOpChecksum()))

    val tlsContext = NoOpTlsContext()
    pipeline.addLast("tls-decryption", TlsDecryptionHandler(tlsContext))
    pipeline.addLast("tls-encryption", TlsEncryptionHandler(tlsContext))


    pipeline.addLast("protocol", TlsProtocolHandler(tlsProtocol, tlsConfig, this))
  }

  /**
   * Switches the checksum algorithm used in the pipeline.
   *
   * Will be called from the [TlsProtocolHandler] once the handshake is complete and a checksum algorithm is negotiated.
   *
   * @param ctx The channel handler context.
   * @param newChecksum The new checksum algorithm to use.
   */
  fun switchChecksum(ctx: ChannelHandlerContext, newChecksum: Checksum) {
    val pipeline = ctx.pipeline()

    if (pipeline.get("checksum-decoder") != null) {
      pipeline.replace("checksum-decoder", "checksum-decoder", ChecksumFrameDecoder(newChecksum))
    }

    if (pipeline.get("checksum-encoder") != null) {
      pipeline.replace("checksum-encoder", "checksum-encoder", ChecksumFrameEncoder(newChecksum))
    }
  }

  /**
   * Switches the TLS context used in the pipeline.
   *
   * Will be called from the [TlsProtocolHandler] once the handshake is complete and a TLS context is established.
   *
   * @param ctx The channel handler context.
   * @param newTlsContext The new TLS context to use.
   */
  fun switchTlsContext(ctx: ChannelHandlerContext, newTlsContext: TlsContext) {
    val pipeline = ctx.pipeline()

    if (pipeline.get("tls-decryption") != null) {
      pipeline.replace("tls-decryption", "tls-decryption", TlsDecryptionHandler(newTlsContext))
    }

    if (pipeline.get("tls-encryption") != null) {
      pipeline.replace("tls-encryption", "tls-encryption", TlsEncryptionHandler(newTlsContext))
    }
  }

  /**
   * Will enable the actual protocol handler after the TLS handshake is complete.
   * Initially a [TlsProtocolHandler] is used to handle the handshake.
   * Once the handshake is complete, this method replaces it with the actual protocol handler provided by [protocolProvider].
   *
   * @param ctx The channel handler context.
   */
  fun enableProtocolHandler(ctx: ChannelHandlerContext) {
    val pipeline = ctx.pipeline()
    if (pipeline.get("protocol") != null) {
      pipeline.replace("protocol", "protocol", protocolProvider())
    }
  }
}
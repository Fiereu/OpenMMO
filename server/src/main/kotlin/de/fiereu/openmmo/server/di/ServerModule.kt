package de.fiereu.openmmo.server.di

import dagger.Module
import dagger.Provides
import de.fiereu.openmmo.protocols.tls.TlsServerProtocol
import de.fiereu.openmmo.server.config.ServerConfig
import de.fiereu.openmmo.server.config.TlsConfig
import de.fiereu.openmmo.server.netty.ChannelHandlerProvider
import de.fiereu.openmmo.server.netty.DefaultChannelHandlerProvider
import de.fiereu.openmmo.server.netty.handlers.ProtocolHandler
import io.netty.channel.EventLoopGroup
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
import kotlinx.coroutines.CoroutineScope
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import javax.inject.Named
import javax.inject.Singleton

@Module
class ServerModule(
  private val coroutineScope: CoroutineScope,
  private val config: ServerConfig,
  private val tlsConfig: TlsConfig,
  private val serverKeyPair: Pair<ECPrivateKey, ECPublicKey>,
  private val protocolProvider: () -> ProtocolHandler
) {

  @Provides
  @Singleton
  fun provideServerConfig(): ServerConfig = config

  @Provides
  @Singleton
  fun provideTlsProtocol(): TlsServerProtocol = TlsServerProtocol(
    serverKeyPair
  )

  @Provides
  @Singleton
  fun provideChannelHandlerProvider(
    serverConfig: ServerConfig,
    tlsProtocol: TlsServerProtocol
  ): ChannelHandlerProvider = DefaultChannelHandlerProvider(
    serverConfig,
    tlsConfig,
    tlsProtocol,
    protocolProvider
  )

  @Provides
  @Singleton
  @Named("boss")
  fun provideBossEventLoopGroup(): EventLoopGroup {
    return MultiThreadIoEventLoopGroup(config.bossThreads, NioIoHandler.newFactory())
  }

  @Provides
  @Singleton
  @Named("worker")
  fun provideWorkerEventLoopGroup(): EventLoopGroup {
    return MultiThreadIoEventLoopGroup(config.workerThreads, NioIoHandler.newFactory())
  }
}
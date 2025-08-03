package de.fiereu.openmmo.server.netty

import de.fiereu.openmmo.server.config.ServerConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

private val log = KotlinLogging.logger {}

@Singleton
class NettyServer @Inject constructor(
    private val config: ServerConfig,
    @param:Named("boss") private val bossGroup: EventLoopGroup,
    @param:Named("worker") private val workerGroup: EventLoopGroup,
    private val channelHandlerProvider: ChannelHandlerProvider
) {
    private var channelFuture: ChannelFuture? = null
    val isRunning: Boolean
        get() = channelFuture?.channel()?.isActive == true

    fun start() {
        log.info { "Starting Netty server on ${config.host}:${config.port}" }
        val bootstrap = ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    channelHandlerProvider.configurePipeline(ch.pipeline())
                }
            })
            .option(ChannelOption.SO_BACKLOG, config.soBacklog)
            .childOption(ChannelOption.SO_KEEPALIVE, config.keepAlive)
            .childOption(ChannelOption.TCP_NODELAY, config.tcpNoDelay)
            .childOption(ChannelOption.SO_RCVBUF, config.receiveBufferSize)
            .childOption(ChannelOption.SO_SNDBUF, config.sendBufferSize)

        channelFuture = bootstrap.bind(config.host, config.port).await()
        log.info { "Netty server started and listening on ${config.host}:${config.port}" }
    }

    fun stop() {
        log.info { "Stopping Netty server" }
        channelFuture?.channel()?.close()?.await()
        bossGroup.shutdownGracefully().await()
        workerGroup.shutdownGracefully().await()
        log.info { "Netty server stopped" }
    }
}
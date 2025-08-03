package de.fiereu.openmmo.server

import de.fiereu.openmmo.server.config.ServerConfig
import de.fiereu.openmmo.server.config.TlsConfig
import de.fiereu.openmmo.server.di.DaggerServerComponent
import de.fiereu.openmmo.server.di.ServerModule
import de.fiereu.openmmo.server.netty.ChannelHandlerProvider
import de.fiereu.openmmo.server.netty.handlers.ProtocolHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

class ServerBuilder {
    private var coroutineScope: CoroutineScope? = null
    private var serverConfig: ServerConfig? = null
    private var tlsConfig: TlsConfig? = null
    private var privateKey: ECPrivateKey? = null
    private var publicKey: ECPublicKey? = null
    private var protocolProvider: (() -> ProtocolHandler)? = null

    fun withCoroutineScope(scope: CoroutineScope): ServerBuilder = apply {
        this.coroutineScope = scope
    }

    fun withConfig(config: ServerConfig): ServerBuilder = apply {
        this.serverConfig = config
    }

    fun withTlsConfig(tlsConfig: TlsConfig): ServerBuilder = apply {
        this.tlsConfig = tlsConfig
    }

    fun withPrivateKey(privateKey: ECPrivateKey): ServerBuilder = apply {
        this.privateKey = privateKey
    }

    fun withPublicKey(publicKey: ECPublicKey): ServerBuilder = apply {
        this.publicKey = publicKey
    }

    fun withChannelHandlerProvider(provider: () -> ProtocolHandler): ServerBuilder = apply {
        this.protocolProvider = provider
    }

    fun build(): Server {
        val coroutineScope = coroutineScope ?: throw IllegalStateException("CoroutineScope must be provided")
        val config = serverConfig ?: throw IllegalStateException("ServerConfig must be provided")
        val tlsConfig = tlsConfig ?: throw IllegalStateException("TlsConfig must be provided")
        val privKey = privateKey ?: throw IllegalStateException("PrivateKey must be provided")
        val pubKey = publicKey ?: throw IllegalStateException("PublicKey must be provided")
        val protocolProvider = protocolProvider ?: throw IllegalStateException("ChannelHandlerProvider must be provided")

        val component = DaggerServerComponent.builder()
            .serverModule(ServerModule(
                coroutineScope,
                config,
                tlsConfig,
                Pair(privKey, pubKey),
                protocolProvider
            ))
            .build()

        return component.server()
    }

    companion object {
        fun create(): ServerBuilder = ServerBuilder()

        fun create(
            config: ServerConfig,
            tlsConfig: TlsConfig,
            privateKey: ECPrivateKey,
            publicKey: ECPublicKey,
            handlerProvider: () -> ProtocolHandler
        ): Server {
            return ServerBuilder()
                .withConfig(config)
                .withTlsConfig(tlsConfig)
                .withPrivateKey(privateKey)
                .withPublicKey(publicKey)
                .withChannelHandlerProvider(handlerProvider)
                .build()
        }
    }
}
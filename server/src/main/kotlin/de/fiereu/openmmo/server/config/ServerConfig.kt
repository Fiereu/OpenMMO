package de.fiereu.openmmo.server.config

import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(
    val host: String = "0.0.0.0",
    val port: Int,
    val workerThreads: Int = Runtime.getRuntime().availableProcessors(),
    val bossThreads: Int = 1,
    val tcpNoDelay: Boolean = true,
    val keepAlive: Boolean = true,
    val soBacklog: Int = 128,
    val receiveBufferSize: Int = 65536,
    val sendBufferSize: Int = 65536,
    val writeTimeoutSeconds: Long = 1500L // 25 minutes default
)
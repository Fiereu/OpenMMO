package de.fiereu.openmmo.server

import de.fiereu.openmmo.server.netty.NettyServer
import io.github.oshai.kotlinlogging.KotlinLogging
import javax.inject.Inject
import javax.inject.Singleton

private val log = KotlinLogging.logger {}

@Singleton
class Server @Inject constructor(
    private val nettyServer: NettyServer
) {
    @Volatile
    private var isStarted = false
    val isRunning: Boolean
        get() = isStarted && nettyServer.isRunning

    fun start() {
        if (isStarted) {
            log.warn { "Server is already started" }
            return
        }

        log.info { "Starting server..." }
        
        try {
            nettyServer.start()
            isStarted = true
            log.info { "Server started successfully" }
        } catch (e: Exception) {
            log.error(e) { "Failed to start server" }
            throw e
        }
    }

    fun stop() {
        if (!isStarted) {
            log.warn { "Server is not started" }
            return
        }

        log.info { "Stopping server..." }
        
        try {
            nettyServer.stop()
            isStarted = false
            log.info { "Server stopped successfully" }
        } catch (e: Exception) {
            log.error(e) { "Error during server shutdown" }
            throw e
        }
    }
}
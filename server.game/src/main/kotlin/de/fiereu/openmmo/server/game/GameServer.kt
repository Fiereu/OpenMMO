package de.fiereu.openmmo.server.game

import de.fiereu.openmmo.protocols.game.GameServerProtocol
import de.fiereu.openmmo.server.ServerBuilder
import de.fiereu.openmmo.server.config.ServerConfig
import de.fiereu.openmmo.server.config.TlsConfig
import de.fiereu.openmmo.server.game.protocol.game.GameProtocolHandler
import de.fiereu.openmmo.server.io.resource
import de.fiereu.openmmo.server.keys.KeyLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun main() {
  val serverConfig = ServerConfig(port = 7777)
  val tlsConfig = TlsConfig(checksumSize = 2)
  val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
  val server =
      ServerBuilder.create()
          .withCoroutineScope(coroutineScope)
          .withConfig(serverConfig)
          .withTlsConfig(tlsConfig)
          .withPublicKey(KeyLoader.loadPemECPublicKey(resource("game.public.pem")))
          .withPrivateKey(KeyLoader.loadPemECPrivateKey(resource("game.private.pem")))
          .withChannelHandlerProvider { GameProtocolHandler(GameServerProtocol(), serverConfig, coroutineScope) }
          .build()
  server.start()
}

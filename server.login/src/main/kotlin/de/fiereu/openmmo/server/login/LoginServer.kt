package de.fiereu.openmmo.server.login

import de.fiereu.openmmo.protocols.tls.LoginServerProtocol
import de.fiereu.openmmo.server.ServerBuilder
import de.fiereu.openmmo.server.config.ServerConfig
import de.fiereu.openmmo.server.config.TlsConfig
import de.fiereu.openmmo.server.io.resource
import de.fiereu.openmmo.server.keys.KeyLoader
import de.fiereu.openmmo.server.login.protocol.login.LoginProtocolHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun main() {
  val serverConfig = ServerConfig(port = 2106)
  val tlsConfig = TlsConfig(checksumSize = 16)
  val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
  val server =
      ServerBuilder.create()
          .withCoroutineScope(coroutineScope)
          .withConfig(serverConfig)
          .withTlsConfig(tlsConfig)
          .withPublicKey(KeyLoader.loadPemECPublicKey(resource("game.public.pem")))
          .withPrivateKey(KeyLoader.loadPemECPrivateKey(resource("game.private.pem")))
          .withChannelHandlerProvider {
            LoginProtocolHandler(LoginServerProtocol(), coroutineScope)
          }
          .build()
  server.start()
}

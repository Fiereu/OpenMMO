package de.fiereu.openmmo.server.login

import de.fiereu.openmmo.protocols.tls.LoginServerProtocol
import de.fiereu.openmmo.server.ServerBuilder
import de.fiereu.openmmo.server.config.ServerConfig
import de.fiereu.openmmo.server.config.TlsConfig
import de.fiereu.openmmo.server.io.resource
import de.fiereu.openmmo.server.keys.KeyLoader
import de.fiereu.openmmo.server.login.protocol.login.LoginProtocolHandler
import de.fiereu.openmmo.server.login.repositories.UserRepository
import de.fiereu.openmmo.server.login.repositories.UserTokenRepository
import de.fiereu.openmmo.server.login.services.UserAuthenticationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jooq.impl.DSL

fun main() {
  val serverConfig = ServerConfig(port = 2106)
  val tlsConfig = TlsConfig(checksumSize = 16)
  val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  val databaseUrl = "jdbc:postgresql://localhost:20011/openmmo_login_db"
  val databaseUser = "openmmo_login_user"
  val databasePassword = "changeMe!"

  val databaseCtx = DSL.using(
    databaseUrl,
    databaseUser,
    databasePassword
  )

  val userRepository = UserRepository(databaseCtx)
  val userTokenRepository = UserTokenRepository(databaseCtx)
  val userAuthenticationService = UserAuthenticationService(userRepository, userTokenRepository)

  val server =
    ServerBuilder.create()
      .withCoroutineScope(coroutineScope)
      .withConfig(serverConfig)
      .withTlsConfig(tlsConfig)
      .withPublicKey(KeyLoader.loadPemECPublicKey(resource("game.public.pem")))
      .withPrivateKey(KeyLoader.loadPemECPrivateKey(resource("game.private.pem")))
      .withChannelHandlerProvider {
        LoginProtocolHandler(
          LoginServerProtocol(),
          coroutineScope,
          userAuthenticationService
        )
      }
      .build()
  server.start()
}
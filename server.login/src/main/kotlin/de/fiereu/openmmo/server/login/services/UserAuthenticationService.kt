package de.fiereu.openmmo.server.login.services

import de.fiereu.openmmo.common.decodeHex
import de.fiereu.openmmo.server.login.jooq.tables.records.UserTokenRecord
import de.fiereu.openmmo.server.login.repositories.UserRepository
import de.fiereu.openmmo.server.login.repositories.UserTokenRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.Optional

/** A Service handling all kinds of User Authentication and Authorization. */
class UserAuthenticationService(
    private val userRepository: UserRepository,
    private val userTokenRepository: UserTokenRepository,
) {
  private val log = KotlinLogging.logger {}

  /**
   * Check if a certain Username and SHA-1 Hashed Password combo exists in the Database.
   *
   * @param username The Username of the User trying to log in.
   * @param passwordSha1 A Hex-String representation of the users' password.
   * @return True if the Username & Password Combo matches one in the Database, otherwise False.
   */
  fun loginPassword(username: String, passwordSha1: String): Boolean {
    log.info { "Login attempt for user $username (password)" }
    val userOpt = userRepository.getUser(username)
    if (userOpt.isEmpty) return false

    val password = passwordSha1.decodeHex()
    if (!password.contentEquals(userOpt.get().password)) {
      log.info { "Login attempt for user $username failed" }
      return false
    }
    return true
  }

  /**
   * Check if a certain Username and Login-Token combo exists in the Database.
   *
   * @param username The Username of the User trying to log in.
   * @param token The Login-Token saved by the client for passwordless login.
   * @return True if the Username & Login-Token Combo matches one in the Database, otherwise False.
   */
  fun loginToken(username: String, token: ByteArray): Boolean {
    log.info { "Login attempt for user $username (token)" }
    val userOpt = userRepository.getUser(username)
    if (userOpt.isEmpty) return false
    val user = userOpt.get()
    val tokenOpt = userTokenRepository.getToken(user)
    if (tokenOpt.isEmpty) return false

    if (!token.contentEquals(tokenOpt.get().token)) {
      log.info { "Login attempt for user $username failed" }
      return false
    }
    return true
  }

  /**
   * Creates a Login-Token for a given Username.
   *
   * @param username The Username for which the Login-Token should be created.
   * @return An [Optional] containing the [UserTokenRecord], empty if the Token couldn't be created.
   */
  fun createToken(username: String): Result<UserTokenRecord> {
    log.info { "Creating new token for user $username" }
    val userOpt = userRepository.getUser(username)
    if (userOpt.isEmpty) {
      return Result.failure(IllegalStateException("User $username doesn't exist."))
    }
    return Result.success(userTokenRepository.createToken(userOpt.get()))
  }
}

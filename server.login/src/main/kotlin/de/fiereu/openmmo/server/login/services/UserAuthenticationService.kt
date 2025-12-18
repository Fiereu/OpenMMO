package de.fiereu.openmmo.server.login.services

import de.fiereu.openmmo.common.decodeHex
import de.fiereu.openmmo.server.login.repositories.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A Service handling all kinds of User Authentication and Authorization.
 */
class UserAuthenticationService(
  private val userRepository: UserRepository,
) {
  private val log: Logger = LoggerFactory.getLogger(UserAuthenticationService::class.java)

  /**
   * Check if a certain Username and SHA-1 Hashed Password combo exists in the Database.
   *
   * @param username The Username of the User trying to log in.
   * @param passwordSha1 A Hex-String representation of the users' password.
   */
  fun loginPassword(username: String, passwordSha1: String): Boolean {
    log.info("Login attempt for user $username (password)")
    val userOpt = userRepository.getUser(username)
    if (userOpt.isEmpty) return false

    val password = passwordSha1.decodeHex()
    if (password != userOpt.get().password) {
      log.info("Login attempt for user $username failed")
      return false
    }
    return true
  }
}
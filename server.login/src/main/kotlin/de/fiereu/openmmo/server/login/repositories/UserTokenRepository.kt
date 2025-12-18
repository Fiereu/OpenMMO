package de.fiereu.openmmo.server.login.repositories

import com.google.common.hash.Hashing
import de.fiereu.openmmo.server.login.jooq.tables.records.UserRecord
import de.fiereu.openmmo.server.login.jooq.tables.records.UserTokenRecord
import de.fiereu.openmmo.server.login.jooq.tables.references.USER_TOKEN
import org.jooq.DSLContext
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * A Repository class wrapping around most of the JOOQ queries needed to interact with UserTokens.
 */
class UserTokenRepository(
  private val ctx: DSLContext
) {

  /**
   * Creates a new Login-Token for a given User.
   *
   * @param user The User which can use this token.
   */
  @OptIn(ExperimentalUuidApi::class)
  fun createToken(user: UserRecord): UserTokenRecord {
    val token = Hashing.sha256()
      .hashBytes(Uuid.random().toByteArray())
      .asBytes()

    val userTokenOpt = ctx.insertInto(USER_TOKEN)
      .set(USER_TOKEN.TOKEN, token)
      .set(USER_TOKEN.USER_ID, user.id)
      .onDuplicateKeyUpdate()
      .set(USER_TOKEN.TOKEN, token)
      .returning()
      .fetchOptional()

    require(userTokenOpt.isPresent) { "Failed to generate new user_token for user ${user.username}" }
    return userTokenOpt.get()
  }

  /**
   * Gets the Login-Token of given User.
   *
   * @param user The [UserRecord] of the user.
   * @return Returns an [Optional] with the given [UserTokenRecord] if found, otherwise its empty.
   */
  fun getToken(user: UserRecord): Optional<UserTokenRecord> = ctx.selectFrom(USER_TOKEN)
    .where(USER_TOKEN.USER_ID.eq(user.id))
    .fetchOptional()
}
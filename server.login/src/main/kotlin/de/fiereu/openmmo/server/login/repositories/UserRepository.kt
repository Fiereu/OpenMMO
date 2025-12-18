package de.fiereu.openmmo.server.login.repositories

import de.fiereu.openmmo.server.login.jooq.tables.records.UserRecord
import de.fiereu.openmmo.server.login.jooq.tables.references.USER
import java.util.*
import org.jooq.DSLContext

/** A Repository class wrapping around most of the JOOQ queries needed to interact with Users. */
class UserRepository(private val ctx: DSLContext) {

  /**
   * Gets a given User.
   *
   * @param username The Username to look up.
   * @return Returns an [Optional] with the given [UserRecord] if found, otherwise its empty.
   */
  fun getUser(username: String): Optional<UserRecord> =
      ctx.selectFrom(USER).where(USER.USERNAME.equal(username)).fetchOptional()
}

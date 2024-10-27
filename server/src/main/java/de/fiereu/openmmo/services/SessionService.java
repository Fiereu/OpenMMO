package de.fiereu.openmmo.services;

import com.github.maltalex.ineter.base.IPAddress;
import de.fiereu.openmmo.db.Database;
import de.fiereu.openmmo.db.jooq.tables.records.ServerTokenRecord;
import de.fiereu.openmmo.db.jooq.tables.records.UserRecord;
import de.fiereu.openmmo.util.UuidUtils;
import lombok.RequiredArgsConstructor;
import org.jooq.postgres.extensions.types.Inet;

import static de.fiereu.openmmo.db.jooq.Tables.SERVER_TOKEN;
import static de.fiereu.openmmo.db.jooq.Tables.USER;

@RequiredArgsConstructor
public class SessionService {
  private final Database database;

  /**
   * Generate a session key for the given user id.
   * This method does not check for permissions or anything else.
   * It just generates a session key.
   *
   * @return the session key or null if the user does not exist
   */
  public byte[] generateSessionKey(int serverId, int userId, IPAddress userIp) {
    UserRecord user = database.ctx()
        .select()
        .from(USER)
        .where(USER.ID.eq(userId))
        .fetchOneInto(USER);
    if (user == null) {
      return null;
    }

    // delete old tokens for this user
    database.ctx()
        .deleteFrom(SERVER_TOKEN)
        .where(SERVER_TOKEN.USER_ID.eq(userId))
        .execute();

    // generate new token
    ServerTokenRecord token = database.ctx().newRecord(SERVER_TOKEN);
    token.setServerId(serverId);
    token.setUserId(userId);
    token.setUserIp(Inet.inet(userIp.toInetAddress()));
    token.store();
    token.refresh(); // refresh to retrieve default values

    return UuidUtils.asBytes(token.getToken());
  }

  public boolean validateSessionKey(int userId, IPAddress userIp, byte[] sessionKey) {
    ServerTokenRecord token = database.ctx()
        .select()
        .from(SERVER_TOKEN)
        .where(SERVER_TOKEN.TOKEN.eq(UuidUtils.asUuid(sessionKey))) // token is validated in the query no need to check it again
        .and(SERVER_TOKEN.USER_ID.eq(userId))
        .and(SERVER_TOKEN.USER_IP.eq(Inet.inet(userIp.toInetAddress())))
        .fetchOneInto(SERVER_TOKEN);

    if (token == null) {
      return false;
    }

    // delete used tokens from the database
    token.delete();
    return true;
  }
}

package de.fiereu.openmmo.loginserver.service;

import de.fiereu.openmmo.db.Database;
import de.fiereu.openmmo.db.jooq.tables.records.UserRecord;
import de.fiereu.openmmo.loginserver.LoginState;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

import static de.fiereu.openmmo.db.jooq.Tables.USER;

@RequiredArgsConstructor
public class LoginService {
  private final Database database;

  public LoginState login(Integer userId, String password) {
    UserRecord user = database.ctx()
        .select()
        .from(USER)
        .where(USER.ID.eq(userId))
        .fetchOneInto(USER);
    if (user == null) {
      return LoginState.INVALID_PASSWORD;
    }

    if (Arrays.equals(user.getPassword(), hexToBytes(password))) {
      return LoginState.AUTHED;
    }

    return LoginState.INVALID_PASSWORD;
  }

  private static byte[] hexToBytes(String hex) {
    int len = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
          + Character.digit(hex.charAt(i+1), 16));
    }
    return data;
  }

  public Optional<Integer> getAccountId(String username) {
    UserRecord user = database.ctx()
        .select()
        .from(USER)
        .where(USER.USERNAME.eq(username))
        .fetchOneInto(USER);
    if (user == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(user.getId());
  }
}

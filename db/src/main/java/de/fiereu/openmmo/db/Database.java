package de.fiereu.openmmo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

public class Database {
  private final Connection connection;

  public Database(
      String url,
      String user,
      String password
  ) {
    try {
      this.connection = DriverManager.getConnection(url, user, password);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public DSLContext ctx() {
    return DSL.using(connection);
  }
}

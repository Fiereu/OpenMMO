package de.fiereu.openmmo.loginserver;

import de.fiereu.openmmo.Server;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.db.Database;
import de.fiereu.openmmo.loginserver.protocol.LoginProtocol;
import de.fiereu.openmmo.loginserver.service.LoginService;
import de.fiereu.openmmo.loginserver.service.ServerService;
import de.fiereu.openmmo.protocol.tls.RootKeyLoader;
import de.fiereu.openmmo.protocol.tls.TlsProtocol;
import de.fiereu.openmmo.services.SessionService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Main {
  public static void main(String[] args) {
    log.info("Starting login server...");
    TlsProtocol tlsProtocol = new TlsProtocol(
        new RootKeyLoader(new File("./game.public"), new File("./game.private"))
    );

    Database database = new Database(
        "jdbc:postgresql://127.0.0.1:5432/openmmo_db",
        "openmmo",
        "openmmo"
    );
    LoginProtocol loginProtocol = new LoginProtocol(
        new LoginService(database),
        new ServerService(database),
        new SessionService(database)
    );

    Server server = new Server(2106, side -> new Session(tlsProtocol, loginProtocol, side));
    server.run();
  }
}

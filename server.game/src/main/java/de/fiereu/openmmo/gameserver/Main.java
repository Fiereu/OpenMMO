package de.fiereu.openmmo.gameserver;

import de.fiereu.openmmo.Server;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.db.Database;
import de.fiereu.openmmo.gameserver.protocol.GameProtocol;
import de.fiereu.openmmo.gameserver.services.CharacterService;
import de.fiereu.openmmo.protocol.tls.RootKeyLoader;
import de.fiereu.openmmo.protocol.tls.TlsProtocol;
import de.fiereu.openmmo.services.SessionService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Main {
  public static void main(String[] args) {
    log.info("Starting game server...");
    TlsProtocol tlsProtocol = new TlsProtocol(
        new RootKeyLoader(new File("./game.public"), new File("./game.private"))
    );

    Database database = new Database(
        "jdbc:postgresql://127.0.0.1:5432/openmmo_db",
        "openmmo",
        "openmmo"
    );
    GameProtocol gameProtocol = new GameProtocol(
        new SessionService(database),
        new CharacterService(database)
    );

    Server server = new Server(7777, side -> new Session(tlsProtocol, gameProtocol, side));
    server.run();
  }
}

package de.fiereu.openmmo.loginserver.service;

import com.github.maltalex.ineter.base.IPAddress;
import de.fiereu.openmmo.db.Database;
import de.fiereu.openmmo.db.jooq.tables.records.ServerPermissionRecord;
import de.fiereu.openmmo.db.jooq.tables.records.UserPermissionRecord;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jooq.Record;
import org.jooq.Result;

import java.util.List;
import java.util.Optional;

import static de.fiereu.openmmo.db.jooq.Tables.*;

@RequiredArgsConstructor
public class ServerService {
  private final Database database;

  public List<GameServer> getGameServers(Integer userId) {
    Result<Record> gameServers = database.ctx()
        .select().from(SERVER)
        .where(SERVER.TYPE.eq("game"))
        .fetch();
    return gameServers.map(record -> new GameServer(
        record.get(SERVER.ID).byteValue(),
        record.get(SERVER.NAME),
        record.get(SERVER.PORT),
        canJoinServer(record.get(SERVER.ID).byteValue(), userId)
    ));
  }

  public boolean canJoinServer(byte serverId, int userId) {
    List<ServerPermissionRecord> serverPermissions = database.ctx()
        .select().from(SERVER_PERMISSION)
        .where(SERVER_PERMISSION.SERVER_ID.eq((int) serverId))
        .fetchInto(ServerPermissionRecord.class);

    // make sure every permission the server requires is in the user's permissions
    // by inner joining the two tables and checking if the result set is the same size as the server's permissions
    Result<Record> userServerPermissions = database.ctx()
        .select()
        .from(SERVER_PERMISSION)
        .innerJoin(USER_PERMISSION)
        .on(SERVER_PERMISSION.PERMISSION_ID.eq(USER_PERMISSION.PERMISSION_ID))
        .where(SERVER_PERMISSION.SERVER_ID.eq((int) serverId))
        .and(USER_PERMISSION.USER_ID.eq(userId))
        .fetch();

    return serverPermissions.size() == userServerPermissions.size();
  }

  public List<GameServerNode> getGameServerNodes(byte gameServerId) {
    Result<Record> gameServerNodes = database.ctx()
        .select().from(SERVER_NODE)
        .join(SERVER).on(SERVER.ID.eq(SERVER_NODE.SERVER_ID))
        .where(SERVER_NODE.SERVER_ID.eq((int) gameServerId))
        .and(SERVER.TYPE.eq("game"))
        .fetch();
    return gameServerNodes
        .map(record -> record.into(SERVER_NODE))
        .stream().map(r -> new GameServerNode(
            r.getId().byteValue(),
            IPAddress.of(r.getIpv4().address()),
            IPAddress.of(r.getIpv6().address()),
            r.getPort()
        )).toList();
  }

  public Optional<GameServer> getGameServer(byte gameServerId, int userId) {
    Record gameServer = database.ctx()
        .select().from(SERVER)
        .where(SERVER.ID.eq((int) gameServerId))
        .and(SERVER.TYPE.eq("game"))
        .fetchOne();
    if (gameServer == null) {
      return Optional.empty();
    }

    return Optional.of(new GameServer(
        gameServer.get(SERVER.ID).byteValue(),
        gameServer.get(SERVER.NAME),
        gameServer.get(SERVER.PORT),
        canJoinServer(gameServer.get(SERVER.ID).byteValue(), userId)
    ));
  }

  @Getter
  @RequiredArgsConstructor
  public static class GameServer {
    private final byte id;
    private final String name;
    private final int port;
    private final boolean joinable;
  }

  @Getter
  @RequiredArgsConstructor
  public static class GameServerNode {
    private final byte id;
    // for ip addresses:
    // Postgres does not distinguish between IPv4 and IPv6 addresses, so we cant assume that the address is IPv4 or IPv6
    // when addresses are encoded for packets they will be automatically converted to IPv4 or IPv6 with a prefix
    // so its onto the client to check if the received addresses match the requirements.
    private final IPAddress address4;
    private final IPAddress address6;
    private final int port;
  }
}

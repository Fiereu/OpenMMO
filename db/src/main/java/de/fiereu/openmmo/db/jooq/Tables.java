/*
 * This file is generated by jOOQ.
 */
package de.fiereu.openmmo.db.jooq;


import de.fiereu.openmmo.db.jooq.tables.Character;
import de.fiereu.openmmo.db.jooq.tables.Container;
import de.fiereu.openmmo.db.jooq.tables.Inventory;
import de.fiereu.openmmo.db.jooq.tables.Item;
import de.fiereu.openmmo.db.jooq.tables.OwnedItem;
import de.fiereu.openmmo.db.jooq.tables.Permissions;
import de.fiereu.openmmo.db.jooq.tables.PgpArmorHeaders;
import de.fiereu.openmmo.db.jooq.tables.Pokemon;
import de.fiereu.openmmo.db.jooq.tables.Server;
import de.fiereu.openmmo.db.jooq.tables.ServerNode;
import de.fiereu.openmmo.db.jooq.tables.ServerPermission;
import de.fiereu.openmmo.db.jooq.tables.ServerToken;
import de.fiereu.openmmo.db.jooq.tables.User;
import de.fiereu.openmmo.db.jooq.tables.UserPermission;
import de.fiereu.openmmo.db.jooq.tables.records.PgpArmorHeadersRecord;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Result;


/**
 * Convenience access to all tables in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Tables {

    /**
     * The table <code>public.character</code>.
     */
    public static final Character CHARACTER = Character.CHARACTER;

    /**
     * The table <code>public.container</code>.
     */
    public static final Container CONTAINER = Container.CONTAINER;

    /**
     * The table <code>public.inventory</code>.
     */
    public static final Inventory INVENTORY = Inventory.INVENTORY;

    /**
     * The table <code>public.item</code>.
     */
    public static final Item ITEM = Item.ITEM;

    /**
     * The table <code>public.owned_item</code>.
     */
    public static final OwnedItem OWNED_ITEM = OwnedItem.OWNED_ITEM;

    /**
     * The table <code>public.permissions</code>.
     */
    public static final Permissions PERMISSIONS = Permissions.PERMISSIONS;

    /**
     * The table <code>public.pgp_armor_headers</code>.
     */
    public static final PgpArmorHeaders PGP_ARMOR_HEADERS = PgpArmorHeaders.PGP_ARMOR_HEADERS;

    /**
     * Call <code>public.pgp_armor_headers</code>.
     */
    public static Result<PgpArmorHeadersRecord> PGP_ARMOR_HEADERS(
          Configuration configuration
        , String __1
    ) {
        return configuration.dsl().selectFrom(de.fiereu.openmmo.db.jooq.tables.PgpArmorHeaders.PGP_ARMOR_HEADERS.call(
              __1
        )).fetch();
    }

    /**
     * Get <code>public.pgp_armor_headers</code> as a table.
     */
    public static PgpArmorHeaders PGP_ARMOR_HEADERS(
          String __1
    ) {
        return de.fiereu.openmmo.db.jooq.tables.PgpArmorHeaders.PGP_ARMOR_HEADERS.call(
            __1
        );
    }

    /**
     * Get <code>public.pgp_armor_headers</code> as a table.
     */
    public static PgpArmorHeaders PGP_ARMOR_HEADERS(
          Field<String> __1
    ) {
        return de.fiereu.openmmo.db.jooq.tables.PgpArmorHeaders.PGP_ARMOR_HEADERS.call(
            __1
        );
    }

    /**
     * The table <code>public.pokemon</code>.
     */
    public static final Pokemon POKEMON = Pokemon.POKEMON;

    /**
     * The table <code>public.server</code>.
     */
    public static final Server SERVER = Server.SERVER;

    /**
     * The table <code>public.server_node</code>.
     */
    public static final ServerNode SERVER_NODE = ServerNode.SERVER_NODE;

    /**
     * The table <code>public.server_permission</code>.
     */
    public static final ServerPermission SERVER_PERMISSION = ServerPermission.SERVER_PERMISSION;

    /**
     * The table <code>public.server_token</code>.
     */
    public static final ServerToken SERVER_TOKEN = ServerToken.SERVER_TOKEN;

    /**
     * The table <code>public.user</code>.
     */
    public static final User USER = User.USER;

    /**
     * The table <code>public.user_permission</code>.
     */
    public static final UserPermission USER_PERMISSION = UserPermission.USER_PERMISSION;
}

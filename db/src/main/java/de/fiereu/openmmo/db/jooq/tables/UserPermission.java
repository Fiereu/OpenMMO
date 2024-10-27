/*
 * This file is generated by jOOQ.
 */
package de.fiereu.openmmo.db.jooq.tables;


import de.fiereu.openmmo.db.jooq.Keys;
import de.fiereu.openmmo.db.jooq.Public;
import de.fiereu.openmmo.db.jooq.tables.Permissions.PermissionsPath;
import de.fiereu.openmmo.db.jooq.tables.User.UserPath;
import de.fiereu.openmmo.db.jooq.tables.records.UserPermissionRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class UserPermission extends TableImpl<UserPermissionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.user_permission</code>
     */
    public static final UserPermission USER_PERMISSION = new UserPermission();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UserPermissionRecord> getRecordType() {
        return UserPermissionRecord.class;
    }

    /**
     * The column <code>public.user_permission.user_id</code>.
     */
    public final TableField<UserPermissionRecord, Integer> USER_ID = createField(DSL.name("user_id"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>public.user_permission.permission_id</code>.
     */
    public final TableField<UserPermissionRecord, Integer> PERMISSION_ID = createField(DSL.name("permission_id"), SQLDataType.INTEGER.nullable(false), this, "");

    private UserPermission(Name alias, Table<UserPermissionRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private UserPermission(Name alias, Table<UserPermissionRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.user_permission</code> table reference
     */
    public UserPermission(String alias) {
        this(DSL.name(alias), USER_PERMISSION);
    }

    /**
     * Create an aliased <code>public.user_permission</code> table reference
     */
    public UserPermission(Name alias) {
        this(alias, USER_PERMISSION);
    }

    /**
     * Create a <code>public.user_permission</code> table reference
     */
    public UserPermission() {
        this(DSL.name("user_permission"), null);
    }

    public <O extends Record> UserPermission(Table<O> path, ForeignKey<O, UserPermissionRecord> childPath, InverseForeignKey<O, UserPermissionRecord> parentPath) {
        super(path, childPath, parentPath, USER_PERMISSION);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class UserPermissionPath extends UserPermission implements Path<UserPermissionRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> UserPermissionPath(Table<O> path, ForeignKey<O, UserPermissionRecord> childPath, InverseForeignKey<O, UserPermissionRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private UserPermissionPath(Name alias, Table<UserPermissionRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public UserPermissionPath as(String alias) {
            return new UserPermissionPath(DSL.name(alias), this);
        }

        @Override
        public UserPermissionPath as(Name alias) {
            return new UserPermissionPath(alias, this);
        }

        @Override
        public UserPermissionPath as(Table<?> alias) {
            return new UserPermissionPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<UserPermissionRecord> getPrimaryKey() {
        return Keys.USER_PERMISSION_PKEY;
    }

    @Override
    public List<ForeignKey<UserPermissionRecord, ?>> getReferences() {
        return Arrays.asList(Keys.USER_PERMISSION__USER_PERMISSION_USER_ID_FKEY, Keys.USER_PERMISSION__USER_PERMISSION_PERMISSION_ID_FKEY);
    }

    private transient UserPath _user;

    /**
     * Get the implicit join path to the <code>public.user</code> table.
     */
    public UserPath user() {
        if (_user == null)
            _user = new UserPath(this, Keys.USER_PERMISSION__USER_PERMISSION_USER_ID_FKEY, null);

        return _user;
    }

    private transient PermissionsPath _permissions;

    /**
     * Get the implicit join path to the <code>public.permissions</code> table.
     */
    public PermissionsPath permissions() {
        if (_permissions == null)
            _permissions = new PermissionsPath(this, Keys.USER_PERMISSION__USER_PERMISSION_PERMISSION_ID_FKEY, null);

        return _permissions;
    }

    @Override
    public UserPermission as(String alias) {
        return new UserPermission(DSL.name(alias), this);
    }

    @Override
    public UserPermission as(Name alias) {
        return new UserPermission(alias, this);
    }

    @Override
    public UserPermission as(Table<?> alias) {
        return new UserPermission(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public UserPermission rename(String name) {
        return new UserPermission(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserPermission rename(Name name) {
        return new UserPermission(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public UserPermission rename(Table<?> name) {
        return new UserPermission(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserPermission where(Condition condition) {
        return new UserPermission(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserPermission where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserPermission where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserPermission where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public UserPermission where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public UserPermission where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public UserPermission where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public UserPermission where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserPermission whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public UserPermission whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
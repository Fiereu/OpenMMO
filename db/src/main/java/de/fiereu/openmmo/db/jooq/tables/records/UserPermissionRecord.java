/*
 * This file is generated by jOOQ.
 */
package de.fiereu.openmmo.db.jooq.tables.records;


import de.fiereu.openmmo.db.jooq.tables.UserPermission;

import org.jooq.Record2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class UserPermissionRecord extends UpdatableRecordImpl<UserPermissionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.user_permission.user_id</code>.
     */
    public void setUserId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.user_permission.user_id</code>.
     */
    public Integer getUserId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.user_permission.permission_id</code>.
     */
    public void setPermissionId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.user_permission.permission_id</code>.
     */
    public Integer getPermissionId() {
        return (Integer) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<Integer, Integer> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserPermissionRecord
     */
    public UserPermissionRecord() {
        super(UserPermission.USER_PERMISSION);
    }

    /**
     * Create a detached, initialised UserPermissionRecord
     */
    public UserPermissionRecord(Integer userId, Integer permissionId) {
        super(UserPermission.USER_PERMISSION);

        setUserId(userId);
        setPermissionId(permissionId);
        resetChangedOnNotNull();
    }
}

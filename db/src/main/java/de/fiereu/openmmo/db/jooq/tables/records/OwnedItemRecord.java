/*
 * This file is generated by jOOQ.
 */
package de.fiereu.openmmo.db.jooq.tables.records;


import de.fiereu.openmmo.db.jooq.tables.OwnedItem;

import org.jooq.Record3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class OwnedItemRecord extends UpdatableRecordImpl<OwnedItemRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.owned_item.item_id</code>.
     */
    public void setItemId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.owned_item.item_id</code>.
     */
    public Integer getItemId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.owned_item.owner_id</code>.
     */
    public void setOwnerId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.owned_item.owner_id</code>.
     */
    public Integer getOwnerId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.owned_item.inventory_id</code>.
     */
    public void setInventoryId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.owned_item.inventory_id</code>.
     */
    public Integer getInventoryId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>public.owned_item.amount</code>.
     */
    public void setAmount(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.owned_item.amount</code>.
     */
    public Integer getAmount() {
        return (Integer) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record3<Integer, Integer, Integer> key() {
        return (Record3) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OwnedItemRecord
     */
    public OwnedItemRecord() {
        super(OwnedItem.OWNED_ITEM);
    }

    /**
     * Create a detached, initialised OwnedItemRecord
     */
    public OwnedItemRecord(Integer itemId, Integer ownerId, Integer inventoryId, Integer amount) {
        super(OwnedItem.OWNED_ITEM);

        setItemId(itemId);
        setOwnerId(ownerId);
        setInventoryId(inventoryId);
        setAmount(amount);
        resetChangedOnNotNull();
    }
}

package de.fiereu.openmmo.gameserver.codecs;

import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.OwnedItemRecord;

public class ItemCodec implements RecordCodec<OwnedItemRecord> {
    @Override
    public void decode(ByteBufEx buffer, OwnedItemRecord object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBufEx buffer, OwnedItemRecord object) {
        byte b = 0;
        if ((b & 1) != 0) {
            buffer.writeIntLE(0);
        }
        buffer.writeShortLE(0);
        buffer.writeShortLE(0);
        buffer.writeByte(object.getInventoryId());
        if ((b & 2) != 0) {
            buffer.writeByte(0);
        }
    }
}

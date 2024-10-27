package de.fiereu.openmmo.gameserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.InventoryRecord;
import de.fiereu.openmmo.db.jooq.tables.records.OwnedItemRecord;
import de.fiereu.openmmo.gameserver.codecs.Codecs;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SendInventoryPacket extends OutgoingPacket {
    private final InventoryRecord inventory;
    private final List<OwnedItemRecord> items;
    private final boolean unk1 = true;

    @Override
    public void encode(ByteBufEx buffer) throws Exception {
        buffer.writeByte(inventory.getId());
        buffer.writeBoolean(unk1);
        buffer.writeShortLE(items.size());
        for (OwnedItemRecord pokemon : items) {
            Codecs.ITEM_CODEC.encode(buffer, pokemon);
        }
    }
}

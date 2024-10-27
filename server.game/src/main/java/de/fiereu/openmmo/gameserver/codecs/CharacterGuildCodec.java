package de.fiereu.openmmo.gameserver.codecs;

import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.CharacterRecord;

public class CharacterGuildCodec implements RecordCodec<CharacterRecord> {
    @Override
    public void decode(ByteBufEx buffer, CharacterRecord object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBufEx buffer, CharacterRecord object) {
        boolean inGuild = false;
        buffer.writeBoolean(inGuild);
        if (!inGuild) {
            return;
        }
        buffer.writeUtf16LE(""); // unused
        buffer.writeIntLE(0);
    }
}

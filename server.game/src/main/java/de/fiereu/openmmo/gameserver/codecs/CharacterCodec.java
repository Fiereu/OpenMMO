package de.fiereu.openmmo.gameserver.codecs;

import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.CharacterRecord;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CharacterCodec implements RecordCodec<CharacterRecord> {
    private final boolean useLong;

    @Override
    public void decode(ByteBufEx buffer, CharacterRecord object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBufEx buffer, CharacterRecord character) {
        buffer.writeIntLE(character.getId());
        buffer.writeUtf16LE(character.getName());
        buffer.writeUtf16LE("");
        buffer.writeIntLE(0);
        buffer.writeByte(0);
        buffer.writeIntLE(0);
        if (useLong) {
            buffer.writeLongLE(0);
        }
        buffer.writeIntLE(0);
        buffer.writeIntLE(0); // unused
        buffer.writeByte(0); // unused
        buffer.writeIntLE(0);
        buffer.writeIntLE(0);
        buffer.writeShortLE(0);
        buffer.writeIntLE(0);
        buffer.writeByte(0);
        buffer.writeByte(0); // unused
        buffer.writeByte(0);
        buffer.writeIntLE(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeShortLE(0);
        buffer.writeByte(0);
        buffer.writeIntLE(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0); // unused
        buffer.writeByte(0); // unused
        buffer.writeByte(0); // unused
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0); // unused
        buffer.writeShortLE(0);
        buffer.writeShortLE(0);
        buffer.writeByte(0); // unused
        buffer.writeByte(0); // unused
        buffer.writeShortLE(0);
        buffer.writeShortLE(0);
        buffer.writeByte(0);
        buffer.writeShortLE(0);
        buffer.writeShortLE(0);
        buffer.writeByte(0); // array size
        buffer.writeBytes(new byte[0]);
    }
}

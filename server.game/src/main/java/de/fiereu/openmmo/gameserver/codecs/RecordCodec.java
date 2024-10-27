package de.fiereu.openmmo.gameserver.codecs;

import de.fiereu.openmmo.bytes.ByteBufEx;
import org.jooq.Record;

public interface RecordCodec<T extends Record> {
    void decode(ByteBufEx buffer, T object);
    void encode(ByteBufEx buffer, T object);
}

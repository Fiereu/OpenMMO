package de.fiereu.openmmo.gameserver.codecs;

import de.fiereu.openmmo.bytes.ByteBufEx;
import org.jooq.Record;

public interface ObjectCodec<T> {
    T decode(ByteBufEx buffer);
    void encode(ByteBufEx buffer, T object);
}

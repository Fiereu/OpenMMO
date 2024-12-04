package de.fiereu.openmmo.gameserver.codecs;

import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.CharacterRecord;
import de.fiereu.openmmo.gameserver.game.SkinType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SkinCodec implements RecordCodec<CharacterRecord> {
    private final boolean useByte;
    private final List<SkinType> skinsToEncode;

    public SkinCodec(boolean useByte, List<SkinType> skinsToEncode) {
        this.useByte = useByte;
        this.skinsToEncode = new ArrayList<>(skinsToEncode);
        assert skinsToEncode.size() <= 16; // make sure we can fit it all in the short mask
    }

    @Override
    public void decode(ByteBufEx buffer, CharacterRecord object) {
        if (useByte) {
            buffer.readByte();
        }
        short mask = buffer.readShortLE();
        for (SkinType type : skinsToEncode) {
            if ((mask & (1 << type.ordinal())) != 0) {
                short combined = buffer.readShortLE();
                short typeValue = (short) (combined & 0x3FFF);
                byte colorValue = (byte) ((combined >> 10) & 0x3F);
                if (typeValue == 0x3FFF) typeValue = -1;
                if (colorValue == 0x3F) colorValue = -1;
                type.setSkin(object, typeValue);
                type.setColor(object, colorValue);
            }
        }
    }

    @Override
    public void encode(ByteBufEx buffer, CharacterRecord object) {
        if (useByte) {
            buffer.writeByte(0);
        }
        skinsToEncode.sort(Comparator.comparingInt(Enum::ordinal));
        short mask = (short) skinsToEncode.stream().mapToInt(SkinType::ordinal).map(i -> 1 << i).reduce(0, (a, b) -> a | b);
        buffer.writeShortLE(mask);
        for (SkinType type : skinsToEncode) {
            short typeValue = type.getSkin(object);
            byte colorValue = type.getColor(object);
            if (typeValue == -1) typeValue = 0x3FFF;
            if (colorValue == -1) colorValue = 0x3F;

            short combined = (short) ((typeValue & 0x3FFF) | ((colorValue << 10) & 0xFC00));
            buffer.writeShortLE(combined);
        }
    }
}

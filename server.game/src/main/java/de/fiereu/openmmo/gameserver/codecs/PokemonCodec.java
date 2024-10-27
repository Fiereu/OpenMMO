package de.fiereu.openmmo.gameserver.codecs;

import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.PokemonRecord;
import de.fiereu.openmmo.gameserver.game.PokemonRarity;

import java.time.ZoneOffset;

public class PokemonCodec implements RecordCodec<PokemonRecord> {
    @Override
    public void decode(ByteBufEx buffer, PokemonRecord object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBufEx buffer, PokemonRecord pkm) {
        buffer.writeIntLE(pkm.getId());
        buffer.writeByte(0);
        buffer.writeIntLE(0);
        buffer.writeIntLE(0); // unused
        buffer.writeByte(pkm.getContainerId());
        buffer.writeShortLE(0);
        buffer.writeShortLE(pkm.getDexId());
        buffer.writeIntLE(pkm.getSeed());
        buffer.writeIntLE(0);
        buffer.writeUtf16LE(""); // ot
        buffer.writeUtf16LE(pkm.getName()); // name
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(pkm.getLevel());
        buffer.writeShortLE(420); // hp
        buffer.writeShortLE(0);
        buffer.writeIntLE(0); // xp
        buffer.writeByte(0);
        buffer.writeShortLE(0);

        Integer[] moves = pkm.getMoves();
        assert moves.length == 4;
        for (int move : moves) {
            buffer.writeShortLE(move & 0xFF);
        }

        Integer[] movesPp = pkm.getMovesPp();
        assert movesPp.length == 4;
        for (int pp : movesPp) {
            buffer.writeByte(pp & 0xFF);
        }

        Integer[] unk1 = {0, 0, 0, 0};
        assert unk1.length == 4;
        for (Integer u : unk1) {
            buffer.writeShortLE(u & 0xFF);
        }

        buffer.writeByte(pkm.getEvHp());
        buffer.writeByte(pkm.getEvAttack());
        buffer.writeByte(pkm.getEvDefense());
        buffer.writeByte(pkm.getEvSpeed());
        buffer.writeByte(pkm.getEvSpecialAttack());
        buffer.writeByte(pkm.getEvSpecialDefense());

        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);
        buffer.writeByte(0);

        // introduce a enum for stats to use here and make it more readable
        int ivs = ((pkm.getIvHp() & 31) << 0) |
                    ((pkm.getIvAttack() & 31) << 5) |
                    ((pkm.getIvDefense() & 31) << 10) |
                    ((pkm.getIvSpeed() & 31) << 15) |
                    ((pkm.getIvSpecialAttack() & 31) << 20) |
                    ((pkm.getIvSpecialDefense() & 31) << 25);
        buffer.writeIntLE(ivs);
        buffer.writeByte(0);
        buffer.writeLongLE(0);

        byte rarity = 0;
        if (pkm.getIsShiny())
            rarity |= 1 << PokemonRarity.SHINY.ordinal();
        if (pkm.getHasHiddenAbility())
            rarity |= 1 << PokemonRarity.HIDDEN_ABILITY.ordinal();
        if (pkm.getIsAlpha())
            rarity |= 1 << PokemonRarity.ALPHA.ordinal();
        if (pkm.getIsSecret())
            rarity |= 1 << PokemonRarity.SECRET.ordinal();

        buffer.writeByte(rarity);
        buffer.writeLongLE(pkm.getCaughtAt().toEpochSecond(ZoneOffset.UTC));
        buffer.writeShortLE(0);
        buffer.writeShortLE(0);
        buffer.writeByte(0); // type
        buffer.writeByte(0); // effect
        buffer.writeByte(0); // effects size
        buffer.writeBytes(new byte[0]); // effect
    }
}

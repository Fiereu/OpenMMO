package de.fiereu.openmmo.gameserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.CharacterRecord;
import de.fiereu.openmmo.gameserver.codecs.Codecs;
import de.fiereu.openmmo.gameserver.game.BattleStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoadPlayerPacket extends OutgoingPacket {
    private final CharacterRecord character;

    @Override
    public void encode(ByteBufEx buffer) throws Exception {
        buffer.writeIntLE(character.getId());
        buffer.writeByte(0);
        Codecs.SKIN_CODEC_1.encode(buffer, character);
        buffer.writeUtf16LE(character.getName());
        buffer.writeByte(0); // region
        buffer.writeByte(6); // bank
        buffer.writeByte(6); // map
        buffer.writeShortLE(8); // x
        buffer.writeShortLE(5); // y
        buffer.writeByte(0); // direction
        buffer.writeByte(0); // flags

        buffer.writeByte(0);
        buffer.writeByte(BattleStatus.NONE.ordinal());

        byte flags = 0x0;
        buffer.writeByte(flags);
        if((flags & 0x01) != 0) {
            buffer.writeByte(0);
        }

        if ((flags & 0x02) != 0) {
            buffer.writeByte(0);
            buffer.writeShortLE(0);
        }

        // has follower
        if ((flags & 0x04) != 0) {
            buffer.writeShortLE(0); // follower pokedex id
        }

        if ((flags & 0x08) != 0) {
            buffer.writeByte(0);
        }

        if ((flags & 0x10) != 0) {
            buffer.writeIntLE(0);
            buffer.writeUtf16LE("");
        }
    }
}

package de.fiereu.openmmo.gameserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.CharacterRecord;
import de.fiereu.openmmo.gameserver.codecs.Codecs;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AcknowledgeCharacterSelectionPacket extends OutgoingPacket {
    private final CharacterRecord character;

    public AcknowledgeCharacterSelectionPacket() {
        this.character = null;
    }

    @Override
    public void encode(ByteBufEx buffer) throws Exception {
        boolean isAuthorized = character != null;
        buffer.writeBoolean(isAuthorized);
        if (!isAuthorized)
            return;

        Codecs.CHARACTER_CODEC.encode(buffer, character);
    }
}

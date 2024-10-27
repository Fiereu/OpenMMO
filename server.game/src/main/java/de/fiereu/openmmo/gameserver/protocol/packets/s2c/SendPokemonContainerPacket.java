package de.fiereu.openmmo.gameserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.db.jooq.tables.records.ContainerRecord;
import de.fiereu.openmmo.db.jooq.tables.records.PokemonRecord;
import de.fiereu.openmmo.gameserver.codecs.Codecs;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SendPokemonContainerPacket extends OutgoingPacket {
    private final ContainerRecord container;
    private final List<PokemonRecord> pokemons;
    private final boolean unk1 = true;
    private final boolean unk2 = true;

    @Override
    public void encode(ByteBufEx buffer) throws Exception {
        buffer.writeByte(container.getId());
        byte flags = 0;
        if (unk1) flags |= 1;
        if (unk2) flags |= 4;
        buffer.writeByte(flags);
        buffer.writeByte(pokemons.size());
        for (PokemonRecord pokemon : pokemons) {
            Codecs.POKEMON_CODEC.encode(buffer, pokemon);
        }
    }
}

package de.fiereu.openmmo.gameserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RenderScreenPacket extends OutgoingPacket {
    private final boolean render;

    @Override
    public void encode(ByteBufEx buffer) throws Exception {
        buffer.writeBoolean(render);
    }
}

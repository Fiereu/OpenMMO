package de.fiereu.openmmo.gameserver.protocol.packets.c2s;

import de.fiereu.openmmo.IncomingPacket;
import de.fiereu.openmmo.Session;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.gameserver.protocol.GameProtocol;
import de.fiereu.openmmo.gameserver.protocol.packets.s2c.LoadPlayerPacket;
import de.fiereu.openmmo.gameserver.protocol.packets.s2c.RenderScreenPacket;

public class RequestPlayerPacket extends IncomingPacket {
    @Override
    public void decode(ByteBufEx buffer) {}

    @Override
    public void handle(Session session) throws Exception {
        session.send(
                new LoadPlayerPacket(
                        session.attr(GameProtocol.ATTRIBUTE_CHARACTER).get()
                ),
                new RenderScreenPacket(true)
        );
    }
}

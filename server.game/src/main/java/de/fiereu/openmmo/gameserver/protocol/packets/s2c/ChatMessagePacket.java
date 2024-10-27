package de.fiereu.openmmo.gameserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.gameserver.codecs.Codecs;
import de.fiereu.openmmo.gameserver.game.chat.ChatMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatMessagePacket extends OutgoingPacket {
    private final ChatMessage chatMessage;

    @Override
    public void encode(ByteBufEx buffer) throws Exception {
        Codecs.CHAT_MESSAGE_CODEC.encode(buffer, chatMessage);
    }
}

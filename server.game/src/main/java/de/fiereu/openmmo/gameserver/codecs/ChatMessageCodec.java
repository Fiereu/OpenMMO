package de.fiereu.openmmo.gameserver.codecs;

import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.gameserver.game.chat.Chat;
import de.fiereu.openmmo.gameserver.game.chat.ChatMessage;

public class ChatMessageCodec implements ObjectCodec<ChatMessage> {
    @Override
    public ChatMessage decode(ByteBufEx buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBufEx buffer, ChatMessage message) {
        Chat chat = message.getChat();
        buffer.writeByte(chat.ordinal());
        if (chat == Chat.LINK) {
            buffer.writeUtf16LE(message.getMessage());
        } else {
            buffer.writeIntLE(0);
            buffer.writeUtf16LE(message.getSender());
            buffer.writeByte(message.getLanguage().ordinal());
            buffer.writeByte(-1);
            buffer.writeUtf16LE(message.getMessage());
        }
    }
}

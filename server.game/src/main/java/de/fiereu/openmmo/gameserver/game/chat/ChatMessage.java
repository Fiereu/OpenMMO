package de.fiereu.openmmo.gameserver.game.chat;

import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.gameserver.game.Language;
import lombok.Getter;

@Getter
public class ChatMessage {
    private final Chat chat;
    private final Language language;
    private final String message;
    private final String sender;

    public static ChatMessage gameNotification(String message) {
        return new ChatMessage(Chat.GAME_NOTIFICATIONS, Language.EN, message, "");
    }

    public ChatMessage(Chat chat, Language language, String message, String sender) {
        this.chat = chat;
        this.language = language;
        this.message = message;
        this.sender = sender;
    }
}

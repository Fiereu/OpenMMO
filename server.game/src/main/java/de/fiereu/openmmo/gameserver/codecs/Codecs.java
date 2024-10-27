package de.fiereu.openmmo.gameserver.codecs;

import de.fiereu.openmmo.gameserver.game.SkinType;

import java.util.List;

public class Codecs {
    public static final CharacterCodec CHARACTER_CODEC = new CharacterCodec(false);
    public static final SkinCodec SKIN_CODEC_1 = new SkinCodec(true, List.of(SkinType.values()));
    public static final SkinCodec SKIN_CODEC_2 = new SkinCodec(false, List.of(SkinType.values()));
    public static final CharacterGuildCodec CHARACTER_GUILD_CODEC = new CharacterGuildCodec();
    public static final PokemonCodec POKEMON_CODEC = new PokemonCodec();
    public static final ChatMessageCodec CHAT_MESSAGE_CODEC = new ChatMessageCodec();
    public static final ItemCodec ITEM_CODEC = new ItemCodec();
}

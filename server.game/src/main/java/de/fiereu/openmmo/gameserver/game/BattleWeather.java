package de.fiereu.openmmo.gameserver.game;

public enum BattleWeather {
    NORMAL(false),
    SUNNY(false),
    RAIN(true),
    SANDSTORM(false),
    FOG(false),
    SNOW(false),
    SHITTY_RAIN(true),
    EVENT_HALLOWEEN_STORM(true),
    EVENT_CNY_DARKNESS(false);

    private final boolean unk;

    BattleWeather(boolean unk) {
        this.unk = unk;
    }
}

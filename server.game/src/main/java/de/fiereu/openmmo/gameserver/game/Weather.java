package de.fiereu.openmmo.gameserver.game;

public enum Weather {
    IN_HOUSE_WEATHER(0),
    SUNNY_WEATHER_WITH_CLOUDS_IN_WATER(1, -1, null),
    REGULAR_WEATHER(2, -1, null),
    RAINY_WEATHER(3, -1, BattleWeather.SHITTY_RAIN),
    THREE_SNOW_FLAKES(4, -1, BattleWeather.SNOW),
    RAIN_WITH_THUNDER(5, -1, BattleWeather.SHITTY_RAIN),
    STEADY_MIST(6),
    STEADY_SNOW(7),
    SAND_STORM(8, -1, BattleWeather.SANDSTORM),
    MIST_FROM_TOP_RIGHT(9),
    DENSE_BRIGHT_MIST(10),
    CLOUDY(11, -1, null),
    UNDERGROUND_FLASHES(12),
    HEAVY_RAIN_WITH_THUNDER(13, -1, BattleWeather.SHITTY_RAIN),
    UNDERWATER_MIST(14),
    UNKNOWN_THUNDER(15, -1, BattleWeather.SHITTY_RAIN),
    DAY_DEPENDANT(19),
    CUSTOM_SNOW(32),
    GEN4_NONE(40, 0, null),
    GEN4_UNK0(41, 1, BattleWeather.NORMAL),
    GEN4_RAIN(42, 2, BattleWeather.SHITTY_RAIN),
    GEN4_HEAVY_RAIN(43, 3, BattleWeather.SHITTY_RAIN),
    GEN4_HEAVY_RAIN_WITH_THUNDER(44, 4, BattleWeather.SHITTY_RAIN),
    GEN4_SNOW(45, 5, BattleWeather.SHITTY_RAIN),
    GEN4_HEAVY_SNOW(46, 6, BattleWeather.SHITTY_RAIN),
    GEN4_HAIL(47, 7, BattleWeather.SNOW),
    GEN4_CLEAR(48, 8, BattleWeather.NORMAL),
    GEN4_ASHDUST(49, 9, BattleWeather.NORMAL),
    GEN4_SANDSTORM(50, 10, BattleWeather.SANDSTORM),
    GEN4_SPECIAL_ICY(51, 11, BattleWeather.NORMAL),
    GEN4_SPECIAL_ROCKS(52, 12, BattleWeather.NORMAL),
    GEN4_UNK(53, 13, BattleWeather.NORMAL),
    GEN4_HEAVY_FOG(54, 14, BattleWeather.FOG),
    GEN4_HEAVY_FOG_WITH_DARKNESS(55, 15, BattleWeather.NORMAL),
    GEN4_CAVE_FLASH(56, 16, BattleWeather.NORMAL),
    GEN4_FOREST_TREE_SHADOWS(57, 23, BattleWeather.NORMAL),
    GEN4_DARKNESS(58, 26, BattleWeather.NORMAL),
    GEN4_GREEN_HAZE(59, 27, BattleWeather.NORMAL),
    ;

    private final int unk1;
    private final int unk2;
    private final BattleWeather battleWeather;

    Weather(int unk1) {
        this(unk1, -1, BattleWeather.NORMAL);
    }

    Weather(int unk1, int unk2, BattleWeather battleWeather) {
        this.unk1 = unk1;
        this.unk2 = unk2;
        this.battleWeather = battleWeather != null ? battleWeather : BattleWeather.NORMAL;
    }
}

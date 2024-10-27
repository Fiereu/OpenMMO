package de.fiereu.openmmo.gameserver.game;

public class Regions {
    public static boolean isGBA(byte regionId) {
        return regionId == 0 || regionId == 1;
    }

    public static boolean isNDS(byte regionId) {
        return regionId == 2 || regionId == 3 || regionId == 4;
    }
}

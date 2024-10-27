package de.fiereu.openmmo.gameserver.game;

public enum Location {
    UNKNOWN_0x00(false),
    UNKNOWN_0x01(true),
    CITY(true),
    ROUTE(true),
    UNDERGROUND(false),
    UNDERWATER(false),
    UNKNOWN_0x06(true),
    UNKNOWN_0x07(false),
    INSIDE(false),
    SECRET_BASE(false);

    private final boolean unk;

    Location(boolean unk) {
        this.unk = unk;
    }
}

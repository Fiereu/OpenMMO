package de.fiereu.openmmo.gameserver.game;

public class Tile2D {
    private final short material;
    /**
     *
     */
    private final byte collision;

    public Tile2D(short material, byte collision) {
        this.material = material;
        this.collision = collision;
    }

    public short encode() {
        return (short) ((material & 0x3FF) | (collision << 10));
    }

    public static Tile2D decode(short encoded) {
        return new Tile2D((short) (encoded & 0x3FF), (byte) (encoded >> 10));
    }
}

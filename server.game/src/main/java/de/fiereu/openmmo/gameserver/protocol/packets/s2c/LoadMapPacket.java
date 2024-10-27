package de.fiereu.openmmo.gameserver.protocol.packets.s2c;

import de.fiereu.openmmo.OutgoingPacket;
import de.fiereu.openmmo.bytes.ByteBufEx;
import de.fiereu.openmmo.gameserver.game.*;
import de.fiereu.openmmo.gameserver.util.Compression;

public class LoadMapPacket extends OutgoingPacket {
    /**
     * Causes the client to respond with a ConfirmMapLoadPacket.
     */
    private final boolean reloadPlayer;
    private final boolean deleteCache;
    private final byte regionId; // maybe use enum for regionIds
    private final byte bankId;
    private final byte mapId;
    private final byte borderWidth;
    private final byte borderHeight;
    private final Tile2D[] borderTiles;
    private final int width;
    private final int height;
    private final int paletteIndex1;
    private final int paletteIndex2;
    private final Lighting lighting;
    private final Weather weather;
    private final Location location;
    private final EncounterType encounterType;

    public LoadMapPacket(boolean reloadPlayer, boolean deleteCache, byte regionId, byte bankId, byte mapId, int width, int height, int paletteIndex1, int paletteIndex2, Lighting lighting, Weather weather, Location location,
                         EncounterType encounterType) {
        this.width = width;
        this.height = height;
        this.paletteIndex1 = paletteIndex1;
        this.paletteIndex2 = paletteIndex2;
        assert Regions.isNDS(regionId);
        this.reloadPlayer = reloadPlayer;
        this.deleteCache = deleteCache;
        this.regionId = regionId;
        this.bankId = bankId;
        this.mapId = mapId;
        this.borderWidth = 0;
        this.borderHeight = 0;
        this.borderTiles = new Tile2D[0];
        this.lighting = lighting;
        this.weather = weather;
        this.location = location;
        this.encounterType = encounterType;
    }

    public LoadMapPacket(boolean reloadPlayer, boolean deleteCache, byte regionId, byte bankId, byte mapId, byte borderWidth, byte borderHeight, Tile2D[] borderTiles, int width, int height, int paletteIndex1, int paletteIndex2, Lighting lighting, Weather weather, Location location,
                         EncounterType encounterType) {
        this.reloadPlayer = reloadPlayer;
        this.deleteCache = deleteCache;
        this.regionId = regionId;
        this.bankId = bankId;
        this.mapId = mapId;
        this.borderWidth = borderWidth;
        this.borderHeight = borderHeight;
        this.borderTiles = borderTiles;
        this.width = width;
        this.height = height;
        this.paletteIndex1 = paletteIndex1;
        this.paletteIndex2 = paletteIndex2;
        assert borderTiles.length == borderWidth * borderHeight;
        assert borderTiles.length == 0 || !Regions.isNDS(regionId);
        this.lighting = lighting;
        this.weather = weather;
        this.location = location;
        this.encounterType = encounterType;
    }

    @Override
    public void encode(ByteBufEx buffer) throws Exception {
        byte flags = 0;
        if (reloadPlayer) {
            flags |= 2;
        }
        if (deleteCache) {
            flags |= 1;
        }
        buffer.writeByte(flags);
        buffer.writeByte(regionId);
        buffer.writeByte(bankId);
        buffer.writeByte(mapId);
        buffer.writeByte(0);
        if (Regions.isNDS(regionId)) {
            buffer.writeShortLE(0);
            byte size = 0;
            buffer.writeByte(size);
            for (byte i = 0; i < size; i++) {
                buffer.writeShortLE(0);
                buffer.writeShortLE(0);
            }

            buffer.writeByte(Lighting.REGULAR.ordinal());
            buffer.writeByte(Weather.REGULAR_WEATHER.ordinal());
            buffer.writeByte(Location.INSIDE.ordinal());
            return;
        }

        buffer.writeIntLE(width);
        buffer.writeIntLE(height);
        buffer.writeIntLE(paletteIndex1);
        buffer.writeIntLE(paletteIndex2);
        buffer.writeByte(borderWidth);
        buffer.writeByte(borderHeight);
        buffer.writeShortLE(0);
        buffer.writeByte(0);
        buffer.writeByte(lighting.ordinal());
        buffer.writeByte(weather.ordinal());
        buffer.writeByte(location.ordinal());
        buffer.writeByte(encounterType.ordinal()); // unused by client

        for (Tile2D tile : borderTiles) {
            buffer.writeShortLE(tile.encode());
        }

        boolean hasUnknown = false;
        buffer.writeBoolean(hasUnknown);
        if (hasUnknown) {
            int size = 0;
            buffer.writeIntLE(size);
            byte[] unknown = new byte[size];
            buffer.writeBytes(Compression.compress(unknown));
        }

        byte connectionCount = 0;
        buffer.writeByte(connectionCount);
        for (byte i = 0; i < connectionCount; i++) {
            buffer.writeByte(0);
            buffer.writeIntLE(0);
            buffer.writeByte(0);
            buffer.writeByte(0);
        }

        boolean hasUnknown2 = false;
        buffer.writeBoolean(hasUnknown2);
        if (hasUnknown2) {
            buffer.writeIntLE(0);
            buffer.writeUtf16LE("");
        }
    }
}

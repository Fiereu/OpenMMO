package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.common.enums.Direction
import de.fiereu.openmmo.common.enums.EntityStatus
import de.fiereu.openmmo.common.enums.SkinSlot
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.game.packets.codecs.SkinSet
import de.fiereu.openmmo.protocols.game.packets.codecs.readSkinsLE
import de.fiereu.openmmo.protocols.game.packets.codecs.writeSkinsLE
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeShortLE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

data class LoadEntityPacket(
    val entityId: Long,
    val skin: SkinSet,
    val name: String,
    val regionId: Int,
    val bankId: Int,
    val mapId: Int,
    val x: Int,
    val y: Int,
    val facing: Direction,
    val status: EntityStatus,
    val hasFollower: Boolean,
    val followerDexId: Short,
)

class LoadEntitySerializer : PacketSerializer<LoadEntityPacket> {
  override fun serialize(packet: LoadEntityPacket, buffer: ByteBuf) =
      buffer.run {
        writeLong(packet.entityId)
        writeByte(0)
        writeSkinsLE(packet.skin)
        writeUtf16LE(packet.name)
        writeByte(packet.regionId)
        writeByte(packet.bankId)
        writeByte(packet.mapId)
        writeShortLE(packet.x)
        writeShortLE(packet.y)
        writeByte(packet.facing.ordinal)
        writeByte(0)

        writeByte(0)
        writeByte(packet.status.ordinal)

        var flags = 0
        if (packet.hasFollower) flags = flags or 0x04
        writeShortLE(flags)
        if (flags and 0x01 != 0) {
          writeByte(0)
        }

        if (flags and 0x02 != 0) {
          writeByte(0)
          writeShortLE(flags)
        }

        if (flags and 0x04 != 0) {
          writeShortLE(packet.followerDexId)
        }

        if (flags and 0x08 != 0) {
          writeByte(0)
        }

        if (flags and 0x10 != 0) {
          writeIntLE(0)
          writeUtf16LE("")
        }
      }
}

class LoadEntityDeserializer : PacketDeserializer<LoadEntityPacket> {
  override fun deserialize(buffer: ByteBuf): LoadEntityPacket =
      buffer.run {
        val entityId = readLong()
        readByte() // Skip padding byte
        val skin = readSkinsLE(SkinSlot.entries)
        val name = readUtf16LE()
        val regionId = readByte().toInt()
        val bankId = readByte().toInt()
        val mapId = readByte().toInt()
        val x = readShortLE()
        val y = readShortLE()
        val facing = Direction.entries[readByte().toInt()]
        readByte() // Skip padding byte

        readByte() // Skip padding byte
        val status = EntityStatus.entries[readByte().toInt()]

        val flags = readUnsignedShortLE()
        if (flags and 0x01 != 0) {
          readByte()
        }

        if (flags and 0x02 != 0) {
          readByte()
          readUnsignedShortLE()
        }

        val hasFollower = (flags and 0x04) != 0
        val followerDexId =
            if (hasFollower) {
              readShortLE()
            } else {
              0
            }

        if (flags and 0x08 != 0) {
          readByte()
        }

        if (flags and 0x10 != 0) {
          readInt() // Skip int value
          readUtf16LE() // Skip string
        }

        LoadEntityPacket(
            entityId = entityId,
            skin = skin,
            name = name,
            regionId = regionId,
            bankId = bankId,
            mapId = mapId,
            x = x.toInt(),
            y = y.toInt(),
            facing = facing,
            status = status,
            hasFollower = hasFollower,
            followerDexId = followerDexId,
        )
      }
}

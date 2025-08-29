package de.fiereu.openmmo.protocols.tls.packets

import com.github.maltalex.ineter.base.IPv4Address
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readIpRangeLE
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeIpRangeLE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

// TODO needs proper investigation and implementation
// Could be split into multiple packets per KickReason
enum class KickReason {
  A,
  IP_RANGE,
  C
}

data class LoginKickPacket(val reason: KickReason?)

class LoginKickPacketSerializer : PacketSerializer<LoginKickPacket> {
  override fun serialize(packet: LoginKickPacket, buffer: ByteBuf) {
    val hasReason = packet.reason != null
    buffer.writeBoolean(hasReason)
    if (!hasReason) return

    buffer.writeByte(packet.reason.ordinal)
    when (packet.reason) {
      KickReason.A -> {
        buffer.writeIntLE(0)
        buffer.writeIntLE(0)
        buffer.writeIntLE(0)
        buffer.writeIntLE(0)
        buffer.writeUtf16LE("")
        buffer.writeUtf16LE("")
        buffer.writeByte(0) // could be a boolean
      }
      KickReason.IP_RANGE -> {
        buffer.writeIntLE(0)
        buffer.writeIpRangeLE(IPv4Address.MIN_ADDR, IPv4Address.MAX_ADDR)
        buffer.writeIntLE(0)
        buffer.writeIntLE(0)
        buffer.writeUtf16LE("")
        buffer.writeUtf16LE("")
        buffer.writeByte(0) // could be a boolean
      }
      KickReason.C -> {
        buffer.writeIntLE(0)
        buffer.writeIntLE(0)
        buffer.writeIntLE(0)
        buffer.writeIntLE(0)
        buffer.writeUtf16LE("")
        buffer.writeUtf16LE("")
        buffer.writeByte(0) // could be a boolean
      }
    }
  }
}

class LoginKickPacketDeserializer : PacketDeserializer<LoginKickPacket> {
  override fun deserialize(buffer: ByteBuf): LoginKickPacket {
    val hasReason = buffer.readBoolean()
    if (!hasReason) {
      return LoginKickPacket(null)
    }

    val reasonOrdinal = buffer.readUnsignedByte().toInt()
    val reason =
        KickReason.entries.getOrNull(reasonOrdinal)
            ?: throw IllegalArgumentException("Unknown kick reason: $reasonOrdinal")

    when (reason) {
      KickReason.A -> {
        buffer.readIntLE()
        buffer.readIntLE()
        buffer.readIntLE()
        buffer.readIntLE()
        buffer.readUtf16LE()
        buffer.readUtf16LE()
        buffer.readByte() // could be a boolean
      }
      KickReason.IP_RANGE -> {
        buffer.readIntLE()
        buffer.readIpRangeLE()
        buffer.readIntLE()
        buffer.readIntLE()
        buffer.readUtf16LE()
        buffer.readUtf16LE()
        buffer.readByte() // could be a boolean
      }
      KickReason.C -> {
        buffer.readIntLE()
        buffer.readIntLE()
        buffer.readIntLE()
        buffer.readIntLE()
        buffer.readUtf16LE()
        buffer.readUtf16LE()
        buffer.readByte() // could be a boolean
      }
    }

    return LoginKickPacket(reason)
  }
}

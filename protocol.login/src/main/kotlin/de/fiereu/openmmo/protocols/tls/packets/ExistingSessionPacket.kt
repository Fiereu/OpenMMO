package de.fiereu.openmmo.protocols.tls.packets

import com.github.maltalex.ineter.base.IPv4Address
import com.github.maltalex.ineter.base.IPv6Address
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE
import de.fiereu.openmmo.protocols.writeIpLE
import de.fiereu.openmmo.protocols.readIpLE
import io.netty.buffer.ByteBuf

data class ExistingSessionPacket(
  val sessionId: Long,
  val sessionKey: ByteArray,
  val serverName: String
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ExistingSessionPacket

    if (sessionId != other.sessionId) return false
    if (!sessionKey.contentEquals(other.sessionKey)) return false
    if (serverName != other.serverName) return false

    return true
  }

  override fun hashCode(): Int {
    var result = sessionId.hashCode()
    result = 31 * result + sessionKey.contentHashCode()
    result = 31 * result + serverName.hashCode()
    return result
  }
}

class SentExistingSessionPacketSerializer : PacketSerializer<ExistingSessionPacket> {
  override fun serialize(packet: ExistingSessionPacket, buffer: ByteBuf) {
    buffer.writeLongLE(packet.sessionId)
    buffer.writeByte(packet.sessionKey.size)
    buffer.writeBytes(packet.sessionKey)

    buffer.writeByte(0)
    buffer.writeUtf16LE(packet.serverName)

    buffer.writeByte(0)
    buffer.writeBytes(ByteArray(0)) // empty byte array for unknown purpose

    buffer.writeUtf16LE("")

    buffer.writeIntLE(0)
    buffer.writeShortLE(0)
    buffer.writeShortLE(0)
    buffer.writeBoolean(false)

    val size: Byte = 0
    buffer.writeByte(size.toInt())
    for (i in 0 until size) {
      buffer.writeByte(i) // not used on client
      buffer.writeIpLE(IPv4Address.MIN_ADDR)
      buffer.writeIpLE(IPv6Address.MIN_ADDR)
      buffer.writeShortLE(0) // port
      buffer.writeByte(0)
    }
  }
}

class SentExistingSessionPacketDeserializer : PacketDeserializer<ExistingSessionPacket> {
  override fun deserialize(buffer: ByteBuf): ExistingSessionPacket {
    val sessionId = buffer.readLongLE()
    val sessionKeyLength = buffer.readUnsignedByte().toInt()
    val sessionKey = ByteArray(sessionKeyLength)
    buffer.readBytes(sessionKey)

    buffer.readByte()
    val serverName = buffer.readUtf16LE()
    
    val unk = ByteArray(buffer.readByte().toInt())
    buffer.readBytes(unk)
    
    buffer.readUtf16LE()

    buffer.readIntLE()
    buffer.readShortLE()
    buffer.readShortLE()
    buffer.readBoolean()

    val size = buffer.readUnsignedByte().toInt()
    for (i in 0 until size) {
      buffer.readByte() // not used on client
      buffer.readIpLE() // IPv4
      buffer.readIpLE() // IPv6
      buffer.readShortLE() // port
      buffer.readByte()
    }

    return ExistingSessionPacket(sessionId, sessionKey, serverName)
  }
}
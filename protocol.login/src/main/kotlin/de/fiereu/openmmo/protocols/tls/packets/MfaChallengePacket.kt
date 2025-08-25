package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

data class MfaChallengePacket(
  val unk: Byte,
  val email: String
)

class MfaChallengePacketSerializer : PacketSerializer<MfaChallengePacket> {
  override fun serialize(packet: MfaChallengePacket, buffer: ByteBuf) {
    buffer.writeByte(packet.unk.toInt())
    buffer.writeUtf16LE(packet.email)
  }
}

class MfaChallengePacketDeserializer : PacketDeserializer<MfaChallengePacket> {
  override fun deserialize(buffer: ByteBuf): MfaChallengePacket {
    val unk = buffer.readByte()
    val email = buffer.readUtf16LE()
    return MfaChallengePacket(unk, email)
  }
}
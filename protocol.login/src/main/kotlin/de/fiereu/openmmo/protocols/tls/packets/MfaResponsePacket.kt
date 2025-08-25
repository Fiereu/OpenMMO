package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

data class MfaResponsePacket(
  val mfaCode: String
)

class MfaResponsePacketSerializer : PacketSerializer<MfaResponsePacket> {
  override fun serialize(packet: MfaResponsePacket, buffer: ByteBuf) {
    buffer.writeUtf16LE(packet.mfaCode)
  }
}

class MfaResponsePacketDeserializer : PacketDeserializer<MfaResponsePacket> {
  override fun deserialize(buffer: ByteBuf): MfaResponsePacket {
    val mfaCode = buffer.readUtf16LE()
    return MfaResponsePacket(mfaCode)
  }
}
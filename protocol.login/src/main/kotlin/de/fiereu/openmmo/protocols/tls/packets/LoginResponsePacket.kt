package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.common.enums.LoginState
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf
import java.time.LocalDateTime

data class LoginResponsePacket(
  val state: LoginState,
  val ratelimitEnd: LocalDateTime?
) {
  constructor(state: LoginState) : this(state, null)
}

class LoginResponsePacketSerializer : PacketSerializer<LoginResponsePacket> {
  override fun serialize(packet: LoginResponsePacket, buffer: ByteBuf) {
    buffer.writeByte(packet.state.id)
    if (packet.state == LoginState.RATE_LIMITED || packet.state == LoginState.RATE_LIMITED_2FA) {
      val epochSeconds = packet.ratelimitEnd?.toEpochSecond(java.time.ZoneOffset.UTC)
        ?: throw IllegalArgumentException("ratelimitEnd must be provided for RATE_LIMITED states")
      buffer.writeLongLE(epochSeconds)
    }
    if (packet.state == LoginState.AUTHED) {
      buffer.writeUtf16LE("")
    }
  }
}

class LoginResponsePacketDeserializer : PacketDeserializer<LoginResponsePacket> {
  override fun deserialize(buffer: ByteBuf): LoginResponsePacket {
    val stateId = buffer.readUnsignedByte().toInt()
    val state = LoginState.entries.find { it.id == stateId } ?: LoginState.SYSTEM_ERROR

    var ratelimitEnd = LocalDateTime.now()
    if (state == LoginState.RATE_LIMITED || state == LoginState.RATE_LIMITED_2FA) {
      val epochSeconds = buffer.readLongLE()
      ratelimitEnd = LocalDateTime.ofEpochSecond(epochSeconds, 0, java.time.ZoneOffset.UTC)
    }
    if (state == LoginState.AUTHED) {
      buffer.readUtf16LE()
    }

    return LoginResponsePacket(state, ratelimitEnd)
  }
}
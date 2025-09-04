package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf
import java.time.LocalDate
import java.time.ZoneId

/*
 * Some info to the packet:
 * The packet is sent in response to a JoinGamePacket BUT
 * seems to also be sent as some kind of time sync packet
 */
data class JoinResponsePacket(
  val canJoin: Boolean,
  val stats: GameStats? = null
  // we dont need TimeInfo here since it can be calculated on the fly
) {
  data class GameStats(
    val playtime: Int,
    val rewardPoints: Int,
    val balance: Int
  )
}

class JoinResponsePacketSerializer : PacketSerializer<JoinResponsePacket> {
  override fun serialize(packet: JoinResponsePacket, buffer: ByteBuf) {
    buffer.writeBoolean(packet.canJoin)
    if (!packet.canJoin) return // no further data is sent

    // the following fields are not used by the client, so we send empty/default values
    buffer.writeUtf16LE("")
    buffer.writeByte(0)

    requireNotNull(packet.stats) { "Stats must be provided if canJoin is true" }
    buffer.writeIntLE(packet.stats.playtime) // Playtime
    buffer.writeIntLE(packet.stats.rewardPoints) // Reward Points
    buffer.writeIntLE(packet.stats.balance) // Balance (pokeyen obvsly)

    // start of current day in epoch seconds
    // used to calculate the current hour
    buffer.writeIntLE(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond().toInt())
    // Used to calculate server time / desync
    buffer.writeIntLE(((System.currentTimeMillis() / 1000).toInt()))
  }
}

class JoinResponsePacketDeserializer : PacketDeserializer<JoinResponsePacketDeserializer.JoinResponsePacket> {
  data class JoinResponsePacket(
    val canJoin: Boolean,
    val stats: GameStats? = null,
    val time: TimeInfo? = null
  ) {

    data class GameStats(
      val playtime: Int,
      val rewardPoints: Int,
      val balance: Int
    )

    data class TimeInfo(
      val serverDayStartSecond: Int,
      val serverCurrentSecond: Int
    )

  }

  override fun deserialize(buffer: ByteBuf): JoinResponsePacket {
    if (!buffer.readBoolean()) {
      return JoinResponsePacket(false, null)
    }

    // unused
    buffer.readUtf16LE()
    buffer.readByte()

    val playtime = buffer.readUnsignedIntLE().toInt()
    val rewardPoints = buffer.readUnsignedIntLE().toInt()
    val balance = buffer.readUnsignedIntLE().toInt()

    val serverDayStartSecond = buffer.readUnsignedIntLE().toInt()
    val serverCurrentSecond = buffer.readUnsignedIntLE().toInt()

    return JoinResponsePacket(
      true,
      JoinResponsePacket.GameStats(playtime, rewardPoints, balance),
      JoinResponsePacket.TimeInfo(serverDayStartSecond, serverCurrentSecond)
    )
  }
}
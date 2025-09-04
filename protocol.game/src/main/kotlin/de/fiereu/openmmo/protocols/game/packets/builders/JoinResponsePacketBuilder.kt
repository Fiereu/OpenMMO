package de.fiereu.openmmo.protocols.game.packets.builders

import de.fiereu.openmmo.protocols.game.packets.JoinResponsePacket

class JoinResponsePacketBuilder(
  private val canJoin: Boolean
) {
  private var playtime: Int = 0
  private var rewardPoints: Int = 0
  private var balance: Int = 0

  companion object {
    fun reject(): JoinResponsePacket {
      return JoinResponsePacket(false)
    }

    fun accept(): JoinResponsePacketBuilder {
      return JoinResponsePacketBuilder(true)
    }
  }

  fun withPlaytime(playtime: Int): JoinResponsePacketBuilder {
    this.playtime = playtime
    return this
  }

  fun withRewardPoints(rewardPoints: Int): JoinResponsePacketBuilder {
    this.rewardPoints = rewardPoints
    return this
  }

  fun withBalance(balance: Int): JoinResponsePacketBuilder {
    this.balance = balance
    return this
  }

  fun build(): JoinResponsePacket {
    require(canJoin) { "Cannot build a JoinResponsePacket with canJoin=false. Use JoinResponsePacketBuilder.reject() instead." }
    return JoinResponsePacket(
      canJoin = true,
      stats = JoinResponsePacket.GameStats(
        playtime = playtime,
        rewardPoints = rewardPoints,
        balance = balance
      )
    )
  }
}
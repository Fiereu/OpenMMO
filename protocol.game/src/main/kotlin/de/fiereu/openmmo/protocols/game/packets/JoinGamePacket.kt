package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.common.enums.Arch
import de.fiereu.openmmo.common.enums.Bitness
import de.fiereu.openmmo.common.enums.Platform
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

data class RomInfo(val code: String, val id: Byte, val type: Byte)

abstract class GameAuthenticationData

/**
 * Used when a player reconnects to the game server after a disconnection. This data typically
 * includes the player's unique identifier and a session key that was issued by the login server to
 * re-establish the session.
 */
data class ReconnectAuthData(val playerId: Long, val sessionKey: ByteArray) :
    GameAuthenticationData()

/**
 * Used when a player is joining the game server for the first time after logging in. This data
 * typically includes the user's unique identifier and a session key that was issued by the login
 * server to authenticate the session.
 */
data class NewAuthData(
    val userId: Int,
    val sessionKey: ByteArray,
) : GameAuthenticationData()

data class JoinGamePacket(
    val authData: GameAuthenticationData,
    val mac: ByteArray,
    val clientRevision: Int,
    val installationRevision: Int,
    val currentChatLanguage: Byte,
    val chatLanguages: Short,
    val matchmakingLanguages: Short,
    val romMask: Byte,
    val roms: List<RomInfo>,
    val clientInfo: Map<Byte, String>,
    val platform: Platform,
    val arch: Arch,
    val bitness: Bitness,
    val unk1: ByteArray
)

class JoinPacketSerializer : PacketSerializer<JoinGamePacket> {
  override fun serialize(packet: JoinGamePacket, buffer: ByteBuf) {
    when (packet.authData) {
      is NewAuthData -> {
        buffer.writeByte(0x00)
        buffer.writeIntLE(packet.authData.userId)
        buffer.writeByte(packet.authData.sessionKey.size)
        buffer.writeBytes(packet.authData.sessionKey)
      }
      is ReconnectAuthData -> {
        buffer.writeByte(0x01)
        buffer.writeLongLE(packet.authData.playerId)
        buffer.writeByte(packet.authData.sessionKey.size)
        buffer.writeBytes(packet.authData.sessionKey)
      }
      else ->
          throw IllegalArgumentException(
              "Unsupported authentication data type: ${packet.authData::class.simpleName}")
    }
    buffer.writeBytes(packet.mac)
    buffer.writeIntLE(packet.clientRevision)
    buffer.writeIntLE(packet.installationRevision)
    buffer.writeByte(packet.currentChatLanguage.toInt())
    buffer.writeShortLE(packet.chatLanguages.toInt())
    buffer.writeShortLE(packet.matchmakingLanguages.toInt())
    buffer.writeByte(packet.romMask.toInt())

    buffer.writeByte(packet.roms.size)
    for (rom in packet.roms) {
      buffer.writeUtf16LE(rom.code)
      buffer.writeByte(rom.id.toInt())
      buffer.writeByte(rom.type.toInt())
    }

    buffer.writeByte(packet.clientInfo.size)
    for ((key, value) in packet.clientInfo) {
      buffer.writeByte(key.toInt())
      buffer.writeUtf16LE(value)
    }

    buffer.writeByte(packet.platform.ordinal)
    buffer.writeByte(packet.arch.ordinal)
    buffer.writeByte(packet.bitness.ordinal)

    buffer.writeByte(packet.unk1.size)
    buffer.writeBytes(packet.unk1)
  }
}

class JoinPacketDeserializer : PacketDeserializer<JoinGamePacket> {
  override fun deserialize(buffer: ByteBuf): JoinGamePacket {
    val authOption = buffer.readByte()
    val authenticationData =
        when (authOption) {
          0x00.toByte() -> {
            val userId = buffer.readIntLE()
            val sessionKeyLength = buffer.readUnsignedByte().toInt()
            val sessionKey = ByteArray(sessionKeyLength)
            buffer.readBytes(sessionKey)
            NewAuthData(userId, sessionKey)
          }
          0x01.toByte() -> {
            val playerId = buffer.readLongLE()
            val sessionKeyLength = buffer.readUnsignedByte().toInt()
            val sessionKey = ByteArray(sessionKeyLength)
            buffer.readBytes(sessionKey)
            ReconnectAuthData(playerId, sessionKey)
          }
          else -> throw IllegalArgumentException("Unsupported authentication option: $authOption")
        }

    val mac = ByteArray(6)
    buffer.readBytes(mac)

    val clientRevision = buffer.readIntLE()
    val installationRevision = buffer.readIntLE()
    val currentChatLanguage = buffer.readByte()
    val chatLanguages = buffer.readShortLE()
    val ignoreLanguages = buffer.readShortLE()
    val romMask = buffer.readByte()

    val romCount = buffer.readUnsignedByte().toInt()
    val roms = mutableListOf<RomInfo>()
    for (i in 0 until romCount) {
      val code = buffer.readUtf16LE()
      val id = buffer.readByte()
      val type = buffer.readByte()
      roms.add(RomInfo(code, id, type))
    }

    val clientInfoCount = buffer.readUnsignedByte().toInt()
    val clientInfo = mutableMapOf<Byte, String>()
    for (i in 0 until clientInfoCount) {
      val key = buffer.readByte()
      val value = buffer.readUtf16LE()
      clientInfo[key] = value
    }

    val platform = Platform.from(buffer.readByte().toInt())
    val arch = Arch.from(buffer.readByte().toInt())
    val bitness = Bitness.from(buffer.readByte().toInt())

    val unk1 = buffer.readByte().toInt()
    val unk2 = ByteArray(unk1)
    buffer.readBytes(unk2)

    return JoinGamePacket(
        authenticationData,
        mac,
        clientRevision,
        installationRevision,
        currentChatLanguage,
        chatLanguages,
        ignoreLanguages,
        romMask,
        roms,
        clientInfo,
        platform,
        arch,
        bitness,
        unk2)
  }
}

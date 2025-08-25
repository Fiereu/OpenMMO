package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.common.enums.Language
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE

interface LoginMethod

data class PasswordLogin(val password: String, val stayLoggedIn: Boolean) : LoginMethod

data class TokenLogin(val token: ByteArray) : LoginMethod

data class LoginRequestPacket(
    val username: String,
    val manualLogin: Boolean,
    val hwid: ByteArray,
    val method: LoginMethod,
    val language: Language,
    val clientRevision: Int,
    val installationRevision: Int,
    val os: UByte,
    val hardwareInfoCache: ByteArray
)

class LoginRequestPacketSerializer : PacketSerializer<LoginRequestPacket> {
  override fun serialize(packet: LoginRequestPacket, buffer: io.netty.buffer.ByteBuf) {
    buffer.writeUtf16LE(packet.username)
    buffer.writeBoolean(packet.manualLogin)
    buffer.writeByte(packet.hwid.size)
    buffer.writeBytes(packet.hwid)

    when (packet.method) {
      is PasswordLogin -> {
        buffer.writeByte(0) // Method type: Password
        buffer.writeUtf16LE(packet.method.password)
        buffer.writeBoolean(packet.method.stayLoggedIn)
      }
      is TokenLogin -> {
        buffer.writeByte(1) // Method type: Token
        buffer.writeByte(packet.method.token.size)
        buffer.writeBytes(packet.method.token)
      }
    }

    buffer.writeUtf16LE(packet.language.code)
    buffer.writeIntLE(packet.clientRevision)
    buffer.writeIntLE(packet.installationRevision)
    buffer.writeByte(packet.os.toInt())
    buffer.writeByte(packet.hardwareInfoCache.size)
    buffer.writeBytes(packet.hardwareInfoCache)
  }
}

class LoginRequestPacketDeserializer : PacketDeserializer<LoginRequestPacket> {
  override fun deserialize(buffer: io.netty.buffer.ByteBuf): LoginRequestPacket {
    val username = buffer.readUtf16LE()
    val manualLogin = buffer.readBoolean()

    val hwidSize = buffer.readUnsignedByte().toInt()
    val hwid = ByteArray(hwidSize)
    buffer.readBytes(hwid)

    val methodType = buffer.readByte().toInt()
    val method: LoginMethod =
        when (methodType) {
          0 -> { // Password
            val password = buffer.readUtf16LE()
            val stayLoggedIn = buffer.readBoolean()
            PasswordLogin(password, stayLoggedIn)
          }

          1 -> { // Token
            val tokenSize = buffer.readUnsignedByte().toInt()
            val token = ByteArray(tokenSize)
            buffer.readBytes(token)
            TokenLogin(token)
          }

          else -> throw IllegalArgumentException("Unknown login method type: $methodType")
        }

    val languageCode = buffer.readUtf16LE()
    val language = Language.fromCode(languageCode)

    val clientRevision = buffer.readIntLE()
    val installationRevision = buffer.readIntLE()
    val os = buffer.readUnsignedByte().toUByte()

    val hardwareInfoCacheSize = buffer.readUnsignedByte().toInt()
    val hardwareInfoCache = ByteArray(hardwareInfoCacheSize)
    buffer.readBytes(hardwareInfoCache)

    return LoginRequestPacket(
        username,
        manualLogin,
        hwid,
        method,
        language,
        clientRevision,
        installationRevision,
        os,
        hardwareInfoCache)
  }
}

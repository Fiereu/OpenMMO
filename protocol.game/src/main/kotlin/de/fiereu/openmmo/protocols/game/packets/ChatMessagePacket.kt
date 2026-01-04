package de.fiereu.openmmo.protocols.game.packets

import de.fiereu.openmmo.common.enums.ChatType
import de.fiereu.openmmo.common.enums.Language
import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

data class ChatMessagePacket(
    val type: ChatType,
    val language: Language?,
    val message: String,
    val sender: String?
)

class ChatMessageSerialize : PacketSerializer<ChatMessagePacket> {
  override fun serialize(packet: ChatMessagePacket, buffer: ByteBuf) {
    buffer.apply {
      writeByte(packet.type.ordinal)
      when (packet.type) {
        ChatType.TEAM -> {
          writeUtf16LE(packet.message)
        }
        else -> {
          require(packet.sender != null) { "sender must not be null" }
          require(packet.language != null) { "language must not be null" }
          writeLongLE(0) // TODO some id
          writeUtf16LE(packet.sender)
          writeByte(packet.language.ordinal)
          writeByte(-1)
          writeUtf16LE(packet.message)
        }
      }
    }
  }
}

class ChatMessageDeserialize : PacketDeserializer<ChatMessagePacket> {
  override fun deserialize(buffer: ByteBuf): ChatMessagePacket {
    when (val type = ChatType.entries[buffer.readByte().toInt()]) {
      ChatType.LINK -> {
        val message = buffer.readUtf16LE()
        return ChatMessagePacket(type, Language.OTHER, message, null)
      }
      else -> {
        buffer.readIntLE()
        val sender = buffer.readUtf16LE()
        val language = Language.entries[buffer.readByte().toInt()]
        buffer.readByte()
        val message = buffer.readUtf16LE()
        return ChatMessagePacket(type, language, message, sender)
      }
    }
  }
}

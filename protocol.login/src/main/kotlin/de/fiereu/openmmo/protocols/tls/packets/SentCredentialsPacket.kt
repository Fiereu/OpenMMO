package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf
import java.util.Base64

/**
 * This packet is sent to the Client to give it the credentials for a user. These credentials are
 * used so no password is stored on the User PC and the Client can automatically log in the user
 * without asking for a password.
 */
data class SentCredentialsPacket(val username: String, val token: String) {
  /**
   * Constructor for when a Login-Token is provided.
   *
   * @param username the username of the user whose credentials we want to send.
   * @param token the Login-Token for the user as a byte array.
   */
  constructor(
    username: String,
    token: ByteArray?
  ) : this(username, token?.let { Base64.getEncoder().encodeToString(it) } ?: "")

  /**
   * Constructor for when no Login-Token is provided. The Client will remove the credentials for this user
   * from its credential list.
   *
   * @param username the username of the user whose credentials we want to remove.
   */
  constructor(username: String) : this(username, "")
}

class SentCredentialsPacketSerializer : PacketSerializer<SentCredentialsPacket> {
  override fun serialize(packet: SentCredentialsPacket, buffer: ByteBuf) {
    buffer.writeUtf16LE(packet.username)
    buffer.writeUtf16LE(packet.token)
  }
}

class SentCredentialsPacketDeserializer : PacketDeserializer<SentCredentialsPacket> {
  override fun deserialize(buffer: ByteBuf): SentCredentialsPacket {
    val username = buffer.readUtf16LE()
    val token = buffer.readUtf16LE()
    return SentCredentialsPacket(username, token)
  }
}

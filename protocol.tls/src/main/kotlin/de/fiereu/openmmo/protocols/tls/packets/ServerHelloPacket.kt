package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.tls.util.sign
import de.fiereu.openmmo.protocols.tls.util.toECPublicKey
import de.fiereu.openmmo.protocols.tls.util.toUncompressedPoint
import io.netty.buffer.ByteBuf
import java.security.PublicKey
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

/**
 * Represents a server hello packet in the TLS handshake.
 *
 * @property publicKey The public key generated for this connection.
 * @property checksumSize The size of the checksum used for this connection.
 */
data class ServerHelloPacket(
  val publicKey: ECPublicKey,
  val checksumSize: Int
)

class ServerHelloPacketSerializer(
  private val serverRootPrivateKey: ECPrivateKey
) : PacketSerializer<ServerHelloPacket> {

  override fun serialize(packet: ServerHelloPacket, buffer: ByteBuf) {
    val publicKeyBytes = packet.publicKey.toUncompressedPoint()
    val signature = serverRootPrivateKey.sign(publicKeyBytes)

    buffer.writeShortLE(publicKeyBytes.size)
    buffer.writeBytes(publicKeyBytes)

    buffer.writeShortLE(signature.size)
    buffer.writeBytes(signature)

    buffer.writeByte(packet.checksumSize)
  }
}

class ServerHelloPacketDeserializer : PacketDeserializer<ServerHelloPacketDeserializer.ServerHelloPacket> {

  data class ServerHelloPacket(
    val clientPublicKey: PublicKey,
    val signature: ByteArray,
    val checksumSize: Int
  )

  override fun deserialize(buffer: ByteBuf): ServerHelloPacket {
    val publicKeySize = buffer.readShortLE().toInt()
    val publicKeyBytes = ByteArray(publicKeySize)
    buffer.readBytes(publicKeyBytes)

    val clientPublicKey = publicKeyBytes.toECPublicKey()

    val signatureSize = buffer.readShortLE().toInt()
    val signature = ByteArray(signatureSize)
    buffer.readBytes(signature)

    val checksumSize = buffer.readByte().toInt()

    return ServerHelloPacket(clientPublicKey, signature, checksumSize)
  }
}

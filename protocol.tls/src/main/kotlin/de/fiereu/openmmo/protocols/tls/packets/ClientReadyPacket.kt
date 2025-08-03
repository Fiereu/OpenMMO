package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import de.fiereu.openmmo.protocols.tls.util.toECPublicKey
import de.fiereu.openmmo.protocols.tls.util.toUncompressedPoint
import io.netty.buffer.ByteBuf
import java.security.interfaces.ECPublicKey

data class ClientReadyPacket(
  val clientPublicKey: ECPublicKey
)

class ClientReadyPacketSerializer : PacketSerializer<ClientReadyPacket> {

  override fun serialize(packet: ClientReadyPacket, buffer: ByteBuf) {
    val publicKeyBytes = packet.clientPublicKey.toUncompressedPoint()

    buffer.writeShortLE(publicKeyBytes.size)
    buffer.writeBytes(publicKeyBytes)
  }
}

class ClientReadyPacketDeserializer : PacketDeserializer<ClientReadyPacket> {

  override fun deserialize(buffer: ByteBuf): ClientReadyPacket {
    val keyBytes = ByteArray(buffer.readShortLE().toInt())
    buffer.readBytes(keyBytes)

    val publicKey = keyBytes.toECPublicKey()

    return ClientReadyPacket(publicKey)
  }
}

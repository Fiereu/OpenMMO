package de.fiereu.openmmo.protocols.tls.packets

import de.fiereu.openmmo.protocols.PacketDeserializer
import de.fiereu.openmmo.protocols.PacketSerializer
import io.netty.buffer.ByteBuf
import kotlin.random.Random

private const val key1 = 3214621489648854472L
private const val key2 = -4214651440992349575L

data class ClientHelloPacket(val timestamp: Long)

class ClientHelloPacketSerializer : PacketSerializer<ClientHelloPacket> {

  override fun serialize(packet: ClientHelloPacket, buffer: ByteBuf) {
    val randomKey = Random.nextLong()

    val xoredRandomKey = randomKey xor key1
    val xoredTimestamp = packet.timestamp xor key2 xor randomKey

    buffer.writeLongLE(xoredRandomKey)
    buffer.writeLongLE(xoredTimestamp)
  }
}

class ClientHelloPacketDeserializer : PacketDeserializer<ClientHelloPacket> {

  override fun deserialize(buffer: ByteBuf): ClientHelloPacket {
    val xoredRandomKey = buffer.readLongLE()
    val xoredTimestamp = buffer.readLongLE()

    val randomKey = xoredRandomKey xor key1
    val timestamp = xoredTimestamp xor key2 xor randomKey

    return ClientHelloPacket(timestamp)
  }
}

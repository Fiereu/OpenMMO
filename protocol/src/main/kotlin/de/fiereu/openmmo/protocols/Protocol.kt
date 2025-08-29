package de.fiereu.openmmo.protocols

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.buffer.ByteBuf
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

/** Value class wrapping a UByte to represent an opcode. */
@JvmInline
value class Opcode(val value: UByte) {
  override fun toString(): String = "0x${value.toString(16).padStart(2, '0').uppercase()}"
}

/**
 * Packet direction enum representing the direction of packet flow. This is needed because depending
 * on the current side (client or server), the same opcode can represent different packets. For
 * example, opcode 0x01 might represent a "LoginRequest" packet on the client side, but a
 * "LoginResponse" packet on the server side.
 */
enum class PacketDirection {
  CLIENT_TO_SERVER,
  SERVER_TO_CLIENT
}

interface PacketSerializer<T : Any> {
  fun serializeObject(packet: Any, buffer: ByteBuf) {
    @Suppress("UNCHECKED_CAST") serialize(packet as T, buffer)
  }

  fun serialize(packet: T, buffer: ByteBuf)
}

interface PacketDeserializer<T : Any> {
  fun deserialize(buffer: ByteBuf): T
}

/**
 * The base class for all protocols. A protocol is a collection of packets that can be sent and
 * received.
 *
 * We cant combine Server and Client protocols because they have different dependencies. For
 * example, the server protocol needs to know about the server's root private key, while the client
 * protocol should not have any knowledge about the server's private key.
 */
abstract class Protocol {
  private val log = KotlinLogging.logger {}
  private val incomingPacketRegistry: MutableMap<Opcode, PacketDeserializer<*>> = mutableMapOf()
  private val outgoingPacketRegistry: MutableMap<Opcode, PacketSerializer<*>> = mutableMapOf()
  private val dataClassToOpcode: MutableMap<KClass<*>, Opcode> = mutableMapOf()

  abstract val async: Boolean

  fun getOpcode(dataClass: KClass<*>): Opcode? = dataClassToOpcode[dataClass]

  fun <T : Any> registerIncoming(opcode: Opcode, deserializer: PacketDeserializer<T>) {
    log.debug {
      "Registering incoming packet: opcode=$opcode, deserializer=${deserializer::class.simpleName}"
    }

    incomingPacketRegistry.put(opcode, deserializer)
  }

  fun <T : Any> registerOutgoing(
      opcode: Opcode,
      serializer: PacketSerializer<T>,
      dataClass: KClass<T>,
  ) {
    log.debug {
      "Registering outgoing packet: opcode=$opcode, serializer=${serializer::class.simpleName}"
    }

    if (dataClassToOpcode.containsKey(dataClass)) {
      // Better to fail fast here than to have hard to debug issues later
      error("Data class ${dataClass.simpleName} is already registered with an opcode!")
    }
    dataClassToOpcode[dataClass] = opcode
    outgoingPacketRegistry.put(opcode, serializer)
  }

  fun getSerializer(opcode: Opcode): PacketSerializer<*>? {
    return outgoingPacketRegistry[opcode]
  }

  fun getDeserializer(opcode: Opcode): PacketDeserializer<*>? {
    return incomingPacketRegistry[opcode]
  }
}

fun Protocol.getDeserializer(opcode: UByte): PacketDeserializer<*>? {
  return this.getDeserializer(Opcode(opcode))
}

fun <T : Any> Protocol.incomingPacket(opcode: UByte, deserializer: PacketDeserializer<T>) {
  this.registerIncoming(Opcode(opcode), deserializer)
}

fun <T : Any> Protocol.outgoingPacket(
    opcode: UByte,
    serializer: PacketSerializer<T>,
    dataClass: KClass<T>
) {
  this.registerOutgoing(Opcode(opcode), serializer, dataClass)
}

package de.fiereu.openmmo.server.netty.handlers

import de.fiereu.openmmo.protocols.PacketDirection
import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.getDeserializer
import de.fiereu.openmmo.server.protocol.PacketEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise

private val log = KotlinLogging.logger {}

abstract class ProtocolHandler(
  private val protocol: Protocol
) : ChannelDuplexHandler() {

  override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
    if (msg !is ByteBuf) {
      log.warn { "Received non-ByteBuf message: ${msg::class.simpleName}" }
      return
    }

    try {
      if (!msg.isReadable) {
        log.warn { "Empty packet received" }
        return
      }
      val opcodeValue = msg.readUnsignedByte().toUByte()

      val deserializer = protocol.getDeserializer(opcodeValue)
      if (deserializer == null) {
        log.warn { "No deserializer found for ${PacketDirection.CLIENT_TO_SERVER} opcode $opcodeValue" }
        return
      }

      val packetData = deserializer.deserialize(msg)

      /*
      We cant call the onPacketReceived function asynchronously directly here,
      because the TLS protocol expects a synchronous flow. Meaning that the client already sends the next packet
      after the ClientReady packet, so if we would process the ClientReady packet asynchronously,
      the next packet would be processed before the ClientReady packet is fully processed.
      -> It would be processed by the TLS protocol before and not by the next protocol (e.g. login protocol).
       */
      onPacketReceived(PacketEvent(ctx, packetData))
    } catch (e: Exception) {
      log.error(e) { "Error processing packet" }
    } finally {
      msg.release()
    }
  }

  override fun write(
    ctx: ChannelHandlerContext,
    msg: Any,
    promise: ChannelPromise
  ) {
    try {
      val opcode = protocol.getOpcode(msg::class)
      if (opcode == null) {
        log.warn { "No opcode found for packet type: ${msg::class.simpleName}" }
        return
      }
      val serializer = protocol.getSerializer(opcode)
      if (serializer == null) {
        log.warn { "No serializer found for ${PacketDirection.SERVER_TO_CLIENT} opcode $opcode" }
        return
      }

      val buffer = ctx.alloc().buffer()
      buffer.writeByte(opcode.value.toInt())
      serializer.serializeObject(msg, buffer)

      ctx.write(buffer, promise)
    } catch (e: Exception) {
      log.error(e) { "Error processing packet" }
    }
  }

  override fun channelActive(ctx: ChannelHandlerContext) {
    onActive(ctx)
  }

  abstract fun onPacketReceived(event: PacketEvent<*>)
  abstract fun onActive(ctx: ChannelHandlerContext)
}
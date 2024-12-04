package de.fiereu.openmmo.handlers;

import de.fiereu.openmmo.BufferedPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Arrays;
import java.util.List;

public class CompressionHandler extends MessageToMessageCodec<BufferedPacket, BufferedPacket> {
  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, BufferedPacket bufferedPacket, List<Object> list) throws Exception {
    ByteBuf buffer = Unpooled.buffer();
    boolean shouldCompress = false; // for now, we don't compress anything

    buffer.writeBoolean(shouldCompress);
    if (shouldCompress) {
      throw new UnsupportedOperationException("Compression is not supported yet");
    } else {
      buffer.writeBytes(bufferedPacket.getData());
    }

    list.add(new BufferedPacket(bufferedPacket.getOpcode(), Arrays.copyOfRange(buffer.array(), buffer.readerIndex(), buffer.writerIndex())));
  }

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, BufferedPacket bufferedPacket, List<Object> list) throws Exception {
    list.add(bufferedPacket);
  }
}

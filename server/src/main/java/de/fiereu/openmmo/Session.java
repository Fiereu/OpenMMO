package de.fiereu.openmmo;

import com.github.maltalex.ineter.base.IPAddress;
import de.fiereu.openmmo.handlers.CompressionHandler;
import de.fiereu.openmmo.handlers.EncryptionHandler;
import de.fiereu.openmmo.protocol.tls.TlsInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
public class Session extends SimpleChannelInboundHandler<BufferedPacket> {
  private enum State {
    UNENCRYPTED,
    ENCRYPTED
  }
  public enum Side {
    CLIENT,
    SERVER
  }
  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
  private final Protocol unencryptedProtocol;
  @Getter
  private final Protocol encryptedProtocol;
  private final Side side;
  private State state = State.UNENCRYPTED;
  @Getter
  private Channel channel;

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    if (cause instanceof ReadTimeoutException) {
      log.warn("Connection with remote address {} timed out.", channel.remoteAddress());
    } else {
      log.error("An exception occurred in connection with remote address {}.", channel.remoteAddress(), cause);
    }

    // TODO: let the client know that the connection is being closed
    // PokeMMO sometimes doesnt handle disconnects properly and just hangs

    this.close();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    this.channel = ctx.channel();
    log.trace("Connection with remote address {} is now active.", channel.remoteAddress());
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, BufferedPacket msg) {
    Protocol protocol = getProtocol();
    Packet packet = protocol.decode(msg, side == Side.CLIENT ? DataFlow.SERVER_TO_CLIENT : DataFlow.CLIENT_TO_SERVER);
    log.trace("Received packet {} from remote address {}.", packet.getClass().getSimpleName(), channel.remoteAddress());

    if (getProtocol().isAsync()) {
      executor.execute(() -> {
        try {
          packet.handle(this);
        } catch (Exception e) {
          log.error("An exception occurred while handling packet.", e);
          ctx.close();
        }
      });
    } else {
      try {
        packet.handle(this);
      } catch (Exception e) {
        log.error("An exception occurred while handling packet.", e);
        ctx.close();
      }
    }
  }

  public void send(Packet... packets) {
    Protocol protocol = getProtocol();
    for (Packet packet : packets) {
      BufferedPacket bufferedPacket;
      try {
        bufferedPacket = protocol.encode(packet, side == Side.CLIENT ? DataFlow.CLIENT_TO_SERVER : DataFlow.SERVER_TO_CLIENT);
      } catch (Exception e) {
        log.error("An exception occurred while encoding packet.", e);
        return;
      }

      log.trace("Sending packet {} to remote address {}.", packet.getClass().getSimpleName(), channel.remoteAddress());
      channel.write(bufferedPacket);
    }

    channel.flush();
  }

  public void enableEncryption(TlsInfo tlsInfo) {
    if (channel.pipeline().get("encryption") != null) {
      throw new IllegalStateException("Connection is already encrypted.");
    }

    state = State.ENCRYPTED;
    channel.pipeline()
        .addAfter("frame", "encryption", new EncryptionHandler(tlsInfo, side));

    log.trace("Connection with remote address {} is now encrypted.", channel.remoteAddress());

    // find a better place for this
    if (getProtocol().isCompressed()) {
      enableCompression();
    }
  }

  public Protocol getProtocol() {
    return state == State.ENCRYPTED ? encryptedProtocol : unencryptedProtocol;
  }

  public <T> Attribute<T> attr(AttributeKey<T> key) {
    return channel.attr(key);
  }

  public IPAddress getRemoteAddress() {
    InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
    return IPAddress.of(inetSocketAddress.getAddress());
  }

  public void close() {
    executor.shutdownNow();
    channel.close();
  }

  public void enableCompression() {
    if (channel.pipeline().get("compression") != null) {
      throw new IllegalStateException("Connection is already compressed.");
    }

    channel.pipeline()
        .addAfter("packet", "compression", new CompressionHandler());
  }
}

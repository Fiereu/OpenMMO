package de.fiereu.openmmo.server.netty

import io.netty.channel.ChannelPipeline

/**
 * Interface for providing channel handlers to be configured in a Netty pipeline.
 * Implementations should define how the pipeline is set up with specific handlers.
 */
interface ChannelHandlerProvider {
  /**
   * Configures the provided [pipeline] with the necessary channel handlers.
   * This method should be implemented to add specific handlers to the pipeline.
   *
   * @param pipeline The [ChannelPipeline] to configure with handlers.
   */
  fun configurePipeline(pipeline: ChannelPipeline)
}
package de.fiereu.openmmo.server.di

import dagger.Component
import de.fiereu.openmmo.server.Server
import de.fiereu.openmmo.server.config.ServerConfig
import de.fiereu.openmmo.server.netty.NettyServer
import javax.inject.Singleton

@Singleton
@Component(modules = [ServerModule::class])
interface ServerComponent {
    fun server(): Server
    fun nettyServer(): NettyServer
    fun serverConfig(): ServerConfig

    @Component.Builder
    interface Builder {
        fun serverModule(module: ServerModule): Builder
        fun build(): ServerComponent
    }
}
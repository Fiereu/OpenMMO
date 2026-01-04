package de.fiereu.openmmo.server.game.protocol.game

import de.fiereu.openmmo.common.CharacterInfo
import de.fiereu.openmmo.common.Pokemon
import de.fiereu.openmmo.common.Tile2D
import de.fiereu.openmmo.common.enums.ChatType
import de.fiereu.openmmo.common.enums.Direction
import de.fiereu.openmmo.common.enums.EVs
import de.fiereu.openmmo.common.enums.EncounterType
import de.fiereu.openmmo.common.enums.EntityStatus
import de.fiereu.openmmo.common.enums.IVs
import de.fiereu.openmmo.common.enums.Language
import de.fiereu.openmmo.common.enums.Lighting
import de.fiereu.openmmo.common.enums.MapType
import de.fiereu.openmmo.common.enums.PokemonContainer
import de.fiereu.openmmo.common.enums.Weather
import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.game.packets.CharacterEntry
import de.fiereu.openmmo.protocols.game.packets.CharacterListPacket
import de.fiereu.openmmo.protocols.game.packets.ChatMessagePacket
import de.fiereu.openmmo.protocols.game.packets.JoinGamePacket
import de.fiereu.openmmo.protocols.game.packets.LoadEntityPacket
import de.fiereu.openmmo.protocols.game.packets.LoadMapPacket
import de.fiereu.openmmo.protocols.game.packets.MapData
import de.fiereu.openmmo.protocols.game.packets.PokemonContainerPacket
import de.fiereu.openmmo.protocols.game.packets.RenderScreenPacket
import de.fiereu.openmmo.protocols.game.packets.RequestCharactersPacket
import de.fiereu.openmmo.protocols.game.packets.RequestPlayerPacket
import de.fiereu.openmmo.protocols.game.packets.SelectCharacterPacket
import de.fiereu.openmmo.protocols.game.packets.SelectedCharacterPacket
import de.fiereu.openmmo.protocols.game.packets.codecs.SkinSet
import de.fiereu.openmmo.server.config.ServerConfig
import de.fiereu.openmmo.server.game.protocol.game.ext.accept
import de.fiereu.openmmo.server.game.protocol.game.ext.buildAndRespond
import de.fiereu.openmmo.server.netty.handlers.ProtocolHandler
import de.fiereu.openmmo.server.protocol.PacketEvent
import de.fiereu.openmmo.server.protocol.respond
import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.ChannelHandlerContext
import java.time.LocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val log = KotlinLogging.logger {}

class GameProtocolHandler(
    protocol: Protocol,
    serverConfig: ServerConfig,
    private val coroutineScope: CoroutineScope,
) : ProtocolHandler(protocol, serverConfig) {

  override fun onActive(ctx: ChannelHandlerContext) {
    log.info { "Client ${ctx.channel().remoteAddress()} connected to game server." }
  }

  @Suppress("unchecked_cast") // Even here i personally check every cast. i live in ur jvm >:(
  override fun onPacketReceived(event: PacketEvent<*>) {
    coroutineScope.launch {
      when (event.packet) {
        is JoinGamePacket -> onJoinGame(event as PacketEvent<JoinGamePacket>)
        is RequestCharactersPacket ->
            onCharacterRequest(event as PacketEvent<RequestCharactersPacket>)
        is SelectCharacterPacket -> onCharacterSelected(event as PacketEvent<SelectCharacterPacket>)
        is RequestPlayerPacket -> onRequestPlayerPacket(event as PacketEvent<RequestPlayerPacket>)
        else -> log.warn { "Unhandled game packet type: ${event.packet::class.simpleName}" }
      }
    }
  }

  fun onJoinGame(event: PacketEvent<JoinGamePacket>) {
    log.info { "Player joined the game." }
    log.debug {
      "This is what the Game knows about you:\n${event.packet.clientInfo.values.joinToString("\n")}"
    }
    // TODO handle player joining the game e.g. authentication

    event.accept().withPlaytime(1337).withRewardPoints(420).withBalance(187).buildAndRespond(event)
  }

  fun onCharacterRequest(event: PacketEvent<RequestCharactersPacket>) {
    log.info { "Player requested character list." }
    // TODO fetch character list from database and send it to the client
    event.respond(
        CharacterListPacket(
            listOf(
                CharacterEntry(
                    CharacterInfo(
                        id = 1,
                        name = "Character",
                        namePrefix = "Test D.",
                        userId = 1,
                        rivalSex = 0,
                        lastLogin = LocalDateTime.of(2025, 1, 1, 12, 0),
                        createdAt = LocalDateTime.of(2020, 1, 1, 12, 0),
                        money = 1_000_000,
                        permissions = 8,
                        remainingSafariSteps = 0,
                        remainingSafariBalls = 0,
                        pcExtraSlots = 0,
                        battleBoxExtraSlots = 0,
                        templateAmount = 0,
                        positionRegionId = 1,
                        positionBankId = 1,
                        positionMapId = 1,
                        positionX = 100,
                        positionY = 100,
                        repelLeft = 0,
                        repelItemId = 0,
                        lureLeft = 0,
                        lureItemId = 0),
                    skinSet = SkinSet(),
                    null,
                    pokemon =
                        listOf(
                            Pokemon(
                                id = 1,
                                container = PokemonContainer.PARTY,
                                containerSlot = 0,
                                dexId = 497,
                                seed = 0,
                                ot = "ich",
                                nickname = "whhhat?",
                                level = 100,
                                hp = 187,
                                xp = 0,
                                eVs = EVs(),
                                iVs = IVs(),
                                moves = emptyList(),
                                isShiny = false,
                                hasHiddenAbility = false,
                                isAlpha = true,
                                isSecret = false,
                                isFatefulEncounter = false,
                                isRaidEncounter = false,
                                caughtAt = LocalDateTime.of(2020, 1, 1, 12, 0),
                            ))))))
  }

  fun onCharacterSelected(event: PacketEvent<SelectCharacterPacket>) {
    log.info { "Player selected character ${event.packet.characterId}" }
    // We need to send the party first.
    // Without the HudGui won't render
    event.respond(
        PokemonContainerPacket(
            PokemonContainer.PARTY, hasChange = true, delete = false, pokemon = emptyList()))
    event.respond(
        SelectedCharacterPacket(
            CharacterInfo(
                id = 1,
                name = "Character",
                namePrefix = "Test D.",
                userId = 1,
                rivalSex = 0,
                lastLogin = LocalDateTime.of(2025, 1, 1, 12, 0),
                createdAt = LocalDateTime.of(2020, 1, 1, 12, 0),
                money = 1_000_000,
                permissions = 8,
                remainingSafariSteps = 0,
                remainingSafariBalls = 0,
                pcExtraSlots = 0,
                battleBoxExtraSlots = 0,
                templateAmount = 0,
                positionRegionId = 1,
                positionBankId = 1,
                positionMapId = 1,
                positionX = 100,
                positionY = 100,
                repelLeft = 0,
                repelItemId = 0,
                lureLeft = 0,
                lureItemId = 0)))
    event.respond(
        ChatMessagePacket(ChatType.GAME_NOTIFICATIONS, Language.EN, "Welcome to OpenMMO!", ""))
    event.respond(
        LoadMapPacket(
            true,
            true,
            0,
            6,
            6,
            MapData.GbaMapData(
                15,
                10,
                12,
                14,
                2,
                2,
                listOf(Tile2D(8, 0), Tile2D(8, 0), Tile2D(8, 0), Tile2D(8, 0)),
                Lighting.REGULAR,
                Weather.IN_HOUSE_WEATHER,
                MapType.INSIDE,
                EncounterType.RANDOM)))
  }

  fun onRequestPlayerPacket(event: PacketEvent<RequestPlayerPacket>) {
    event.respond(
        LoadEntityPacket(
            1,
            SkinSet(),
            "Character",
            0,
            6,
            6,
            8,
            5,
            Direction.SOUTH,
            EntityStatus.NONE,
            hasFollower = false,
            0))
    event.respond(RenderScreenPacket(true))
  }
}

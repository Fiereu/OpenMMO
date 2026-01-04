package de.fiereu.openmmo.protocols.game

import de.fiereu.openmmo.protocols.Protocol
import de.fiereu.openmmo.protocols.game.packets.CharacterListDeserializer
import de.fiereu.openmmo.protocols.game.packets.CharacterListSerializer
import de.fiereu.openmmo.protocols.game.packets.ChatMessageDeserialize
import de.fiereu.openmmo.protocols.game.packets.ChatMessageSerialize
import de.fiereu.openmmo.protocols.game.packets.JoinPacketDeserializer
import de.fiereu.openmmo.protocols.game.packets.JoinPacketSerializer
import de.fiereu.openmmo.protocols.game.packets.JoinResponseDeserializer
import de.fiereu.openmmo.protocols.game.packets.JoinResponseSerializer
import de.fiereu.openmmo.protocols.game.packets.LoadEntityDeserializer
import de.fiereu.openmmo.protocols.game.packets.LoadEntitySerializer
import de.fiereu.openmmo.protocols.game.packets.LoadMapPacketDeserializer
import de.fiereu.openmmo.protocols.game.packets.LoadMapPacketSerializer
import de.fiereu.openmmo.protocols.game.packets.PokemonContainerDeserializer
import de.fiereu.openmmo.protocols.game.packets.PokemonContainerSerializer
import de.fiereu.openmmo.protocols.game.packets.RenderScreenDeserializer
import de.fiereu.openmmo.protocols.game.packets.RenderScreenSerializer
import de.fiereu.openmmo.protocols.game.packets.RequestCharactersDeserializer
import de.fiereu.openmmo.protocols.game.packets.RequestCharactersPacketSerializer
import de.fiereu.openmmo.protocols.game.packets.RequestPlayerDeserializePacket
import de.fiereu.openmmo.protocols.game.packets.RequestPlayerSerializer
import de.fiereu.openmmo.protocols.game.packets.SelectCharacterDeserializer
import de.fiereu.openmmo.protocols.game.packets.SelectCharacterSerializer
import de.fiereu.openmmo.protocols.game.packets.SelectedCharacterDeserializer
import de.fiereu.openmmo.protocols.game.packets.SelectedCharacterSerializer
import de.fiereu.openmmo.protocols.incomingPacket
import de.fiereu.openmmo.protocols.outgoingPacket

/**
 * Game Protocol implementation. This protocol handles in-game communication after the TLS handshake
 * is complete.
 */
abstract class GameProtocol : Protocol() {
  override val async: Boolean = true
  override val compressed: Boolean = true
}

class GameServerProtocol : GameProtocol() {
  init {
    incomingPacket(0x01u, JoinPacketDeserializer())
    outgoingPacket(0x01u, JoinResponseSerializer())
    incomingPacket(0x02u, RequestCharactersDeserializer())
    outgoingPacket(0x02u, CharacterListSerializer())
    incomingPacket(0x04u, SelectCharacterDeserializer())
    outgoingPacket(0x04u, SelectedCharacterSerializer())
    incomingPacket(0x05u, RequestPlayerDeserializePacket())
    outgoingPacket(0x05u, LoadEntitySerializer())
    outgoingPacket(0x09u, ChatMessageSerialize())
    outgoingPacket(0x10u, LoadMapPacketSerializer())
    outgoingPacket(0x13u, PokemonContainerSerializer())
    outgoingPacket(0xB4u, RenderScreenSerializer())
  }
}

class GameClientProtocol : GameProtocol() {
  init {
    outgoingPacket(0x01u, JoinPacketSerializer())
    incomingPacket(0x01u, JoinResponseDeserializer())
    outgoingPacket(0x02u, RequestCharactersPacketSerializer())
    incomingPacket(0x02u, CharacterListDeserializer())
    outgoingPacket(0x04u, SelectCharacterSerializer())
    incomingPacket(0x04u, SelectedCharacterDeserializer())
    outgoingPacket(0x05u, RequestPlayerSerializer())
    incomingPacket(0x05u, LoadEntityDeserializer())
    incomingPacket(0x09u, ChatMessageDeserialize())
    incomingPacket(0x10u, LoadMapPacketDeserializer())
    incomingPacket(0x13u, PokemonContainerDeserializer())
    incomingPacket(0xB4u, RenderScreenDeserializer())
  }
}

package de.fiereu.openmmo.protocols.game.packets.codecs

import de.fiereu.openmmo.common.Pokemon
import de.fiereu.openmmo.common.PokemonMove
import de.fiereu.openmmo.common.enums.EVs
import de.fiereu.openmmo.common.enums.PokemonContainer
import de.fiereu.openmmo.common.enums.PokemonRarity
import de.fiereu.openmmo.common.enums.compress
import de.fiereu.openmmo.common.enums.decompressIVs
import de.fiereu.openmmo.protocols.readEpochSeconds
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeByte
import de.fiereu.openmmo.protocols.writeEpochSecond
import de.fiereu.openmmo.protocols.writeShortLE
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

fun ByteBuf.writePokemonLE(pokemon: Pokemon) = apply {
  writeLongLE(pokemon.id)
  writeByte(0)
  writeLongLE(0)
  writeLongLE(0) // unused
  writeByte(pokemon.container.ordinal)
  writeShortLE(pokemon.containerSlot)
  writeShortLE(pokemon.dexId)
  writeIntLE(pokemon.seed)
  writeLongLE(0) // unused
  writeUtf16LE(pokemon.ot)
  writeUtf16LE(pokemon.nickname)
  writeByte(0)
  writeByte(0)
  writeByte(pokemon.level)
  writeShortLE(pokemon.hp)
  writeShortLE(0)
  writeIntLE(pokemon.xp)
  writeByte(0)
  writeShortLE(0)
  pokemon.moves.forEach { move -> writeShortLE(move.id) }
  pokemon.moves.forEach { move -> writeByte(move.pp) }
  arrayOf(0, 0, 0, 0).forEach { writeShortLE(it) }

  writeByte(pokemon.eVs.hp)
  writeByte(pokemon.eVs.atk)
  writeByte(pokemon.eVs.def)
  writeByte(pokemon.eVs.spd)
  writeByte(pokemon.eVs.spAtk)
  writeByte(pokemon.eVs.spDef)

  writeByte(0)
  writeByte(0)
  writeByte(0)
  writeByte(0)
  writeByte(0)
  writeByte(0)
  writeByte(0)
  writeByte(0)
  writeByte(0)
  writeByte(0)
  writeByte(0)

  writeIntLE(pokemon.iVs.compress())
  writeByte(0)
  writeLongLE(0)

  var rarityMask = 0
  if (pokemon.isShiny) rarityMask = rarityMask or (1 shl PokemonRarity.SHINY.ordinal)
  if (pokemon.hasHiddenAbility)
      rarityMask = rarityMask or (1 shl PokemonRarity.HIDDEN_ABILITY.ordinal)
  if (pokemon.isAlpha) rarityMask = rarityMask or (1 shl PokemonRarity.ALPHA.ordinal)
  if (pokemon.isSecret) rarityMask = rarityMask or (1 shl PokemonRarity.SECRET.ordinal)
  if (pokemon.isFatefulEncounter)
      rarityMask = rarityMask or (1 shl PokemonRarity.FATEFUL_ENCOUNTER.ordinal)
  if (pokemon.isRaidEncounter)
      rarityMask = rarityMask or (1 shl PokemonRarity.RAID_ENCOUNTER.ordinal)
  writeShortLE(rarityMask)

  writeEpochSecond(pokemon.caughtAt)
  writeShortLE(0)
  writeByte(0)
  writeByte(0)

  val unk = ByteArray(0)
  writeByte(unk.size)
  writeBytes(unk)

  writeBytes(ByteArray(12) { 0.toByte() })
}

fun ByteBuf.readPokemonLE(): Pokemon {
  val id = readLongLE()
  readByte()
  readLongLE()
  readLongLE() // unused
  val container = PokemonContainer.entries[readUnsignedByte().toInt()]
  val containerSlot = readShortLE()
  val dexId = readUnsignedShortLE().toInt()
  val seed = readIntLE()
  readLongLE() // unused
  val ot = readUtf16LE()
  val nickname = readUtf16LE()
  readByte()
  readByte()
  val level = readByte()
  val currentHp = readShortLE()
  readShortLE()
  val xp = readIntLE()
  readByte()
  readShortLE()
  val moves = List(4) { PokemonMove(readShortLE(), 0) }
  moves.forEach { move -> move.pp = readByte() }
  repeat(4) { readByte() }
  val eVs =
      EVs().apply {
        hp = readByte().toInt()
        atk = readByte().toInt()
        def = readByte().toInt()
        spd = readByte().toInt()
        spAtk = readByte().toInt()
        spDef = readByte().toInt()
      }
  readByte()
  readByte()
  readByte()
  readByte()
  readByte()
  readByte()
  readByte()
  readByte()
  readByte()
  readByte()
  readByte()
  readByte() // unused
  readByte()
  readByte()
  readByte()
  readByte()
  readByte()
  val iVs = decompressIVs(readIntLE())
  readByte()
  readLongLE()

  val rarityMask = readUnsignedShortLE()
  val isShiny = (rarityMask and (1 shl PokemonRarity.SHINY.ordinal)) != 0
  val hasHiddenAbility = (rarityMask and (1 shl PokemonRarity.HIDDEN_ABILITY.ordinal)) != 0
  val isAlpha = (rarityMask and (1 shl PokemonRarity.ALPHA.ordinal)) != 0
  val isSecret = (rarityMask and (1 shl PokemonRarity.SECRET.ordinal)) != 0
  val isFatefulEncounter = (rarityMask and (1 shl PokemonRarity.FATEFUL_ENCOUNTER.ordinal)) != 0
  val isRaidEncounter = (rarityMask and (1 shl PokemonRarity.RAID_ENCOUNTER.ordinal)) != 0

  val caughtAt = readEpochSeconds()
  readShortLE()
  readByte()
  readByte()

  val unknown = ByteArray(readByte().toInt())
  readBytes(unknown)

  return Pokemon(
      id,
      container,
      containerSlot,
      dexId,
      seed,
      ot,
      nickname,
      level,
      currentHp,
      xp,
      eVs,
      iVs,
      moves,
      isShiny,
      hasHiddenAbility,
      isAlpha,
      isSecret,
      isFatefulEncounter,
      isRaidEncounter,
      caughtAt)
}

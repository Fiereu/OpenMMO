package de.fiereu.openmmo.common.enums

import java.util.*

enum class PokemonStat {
  HP,
  ATTACK,
  DEFENSE,
  SP_ATTACK,
  SP_DEFENSE,
  SPEED
}

open class PokemonStats(private val individualCap: Int, private val totalCap: Int) :
    EnumMap<PokemonStat, Byte>(PokemonStat.entries.associateWith { 0.toByte() }) {

  val total
    get() = hp + atk + def + spAtk + spDef + spd

  private fun cr(value: Int): Byte {
    if (value <= 0) error("Value can't be <= 0")
    if (value > individualCap) error("Value exceeds individual capacity")
    if (total > totalCap) error("Total value exceeds capacity")
    return value.toByte()
  }

  var hp
    get() = get(PokemonStat.HP)!!.toInt()
    set(value) = set(PokemonStat.HP, cr(value))

  var atk
    get() = get(PokemonStat.ATTACK)!!.toInt()
    set(value) = set(PokemonStat.ATTACK, cr(value))

  var def
    get() = get(PokemonStat.DEFENSE)!!.toInt()
    set(value) = set(PokemonStat.DEFENSE, cr(value))

  var spAtk
    get() = get(PokemonStat.SP_ATTACK)!!.toInt()
    set(value) = set(PokemonStat.SP_ATTACK, cr(value))

  var spDef
    get() = get(PokemonStat.SP_DEFENSE)!!.toInt()
    set(value) = set(PokemonStat.SP_DEFENSE, cr(value))

  var spd
    get() = get(PokemonStat.SPEED)!!.toInt()
    set(value) = set(PokemonStat.SPEED, cr(value))
}

class EVs : PokemonStats(252, 510)

class IVs : PokemonStats(31, 155)

fun IVs.compress(): Int =
    (((hp and 31) shl 0) or
        ((atk and 31) shl 5) or
        ((def and 31) shl 10) or
        ((spAtk and 31) shl 15) or
        ((spDef and 31) shl 20) or
        ((spd and 31) shl 25))

fun decompressIVs(value: Int): IVs =
    IVs().apply {
      hp = (value shr 0) and 31
      atk = (value shr 5) and 31
      def = (value shr 10) and 31
      spAtk = (value shr 15) and 31
      spDef = (value shr 20) and 31
      spd = (value shr 25) and 31
    }

enum class PokemonBattleStat {
  HP,
  ATTACK,
  DEFENSE,
  SP_ATTACK,
  SP_DEFENSE,
  SPEED,
  ACCURACY,
  EVASION
}

// Not sure if this is correct just copying from EVs here
class PokemonBattleStats :
    EnumMap<PokemonBattleStat, Byte>(PokemonBattleStat.entries.associateWith { 0.toByte() }) {
  private val individualCap: Int = 252
  private val totalCap: Int = 510

  val total
    get() = hp + atk + def + spAtk + spDef + spd + acc + evs

  private fun cr(value: Int): Byte {
    if (value <= 0) error("Value can't be <= 0")
    if (value > individualCap) error("Value exceeds individual capacity")
    if (total > totalCap) error("Total value exceeds capacity")
    return value.toByte()
  }

  var hp
    get() = get(PokemonBattleStat.HP)!!.toInt()
    set(value) = set(PokemonBattleStat.HP, cr(value))

  var atk
    get() = get(PokemonBattleStat.ATTACK)!!.toInt()
    set(value) = set(PokemonBattleStat.ATTACK, cr(value))

  var def
    get() = get(PokemonBattleStat.DEFENSE)!!.toInt()
    set(value) = set(PokemonBattleStat.DEFENSE, cr(value))

  var spAtk
    get() = get(PokemonBattleStat.SP_ATTACK)!!.toInt()
    set(value) = set(PokemonBattleStat.SP_ATTACK, cr(value))

  var spDef
    get() = get(PokemonBattleStat.SP_DEFENSE)!!.toInt()
    set(value) = set(PokemonBattleStat.SP_DEFENSE, cr(value))

  var spd
    get() = get(PokemonBattleStat.SPEED)!!.toInt()
    set(value) = set(PokemonBattleStat.SPEED, cr(value))

  var acc
    get() = get(PokemonBattleStat.ACCURACY)!!.toInt()
    set(value) = set(PokemonBattleStat.SPEED, value.toByte())

  var evs
    get() = get(PokemonBattleStat.EVASION)!!.toInt()
    set(value) = set(PokemonBattleStat.EVASION, value.toByte())

  init {
    // just guessing that "252 / 2" => 100%
    acc = 252 / 2
    evs = 252 / 2
  }
}

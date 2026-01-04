package de.fiereu.openmmo.common

import de.fiereu.openmmo.common.enums.EVs
import de.fiereu.openmmo.common.enums.IVs
import de.fiereu.openmmo.common.enums.PokemonContainer
import de.fiereu.openmmo.common.enums.PokemonNature
import java.time.LocalDateTime

data class Pokemon(
    val id: Long,
    val container: PokemonContainer,
    val containerSlot: Short,
    val dexId: Int,
    val seed: Int,
    val ot: String,
    val nickname: String,
    val level: Byte,
    val hp: Short,
    val xp: Int,
    val eVs: EVs,
    val iVs: IVs,
    val moves: List<PokemonMove>,
    val isShiny: Boolean,
    val hasHiddenAbility: Boolean,
    val isAlpha: Boolean,
    val isSecret: Boolean,
    val isFatefulEncounter: Boolean,
    val isRaidEncounter: Boolean,
    val caughtAt: LocalDateTime
) {
  val nature: PokemonNature = PokemonNature.entries[seed % PokemonNature.entries.size]

  init {
    require(moves.size <= 4) { "A Pokemon can't have more than 4 moves" }
  }
}

data class PokemonMove(val id: Short, var pp: Byte)

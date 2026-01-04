package de.fiereu.openmmo.common.enums

enum class PokemonNature(
    val increasedStat: PokemonStat,
    val decreasedStat: PokemonStat,
    val favoriteFlavor: Flavor,
    val dislikedFlavor: Flavor
) {
  HARDY(PokemonStat.ATTACK, PokemonStat.ATTACK, Flavor.SPICY, Flavor.SPICY),
  LONELY(PokemonStat.ATTACK, PokemonStat.DEFENSE, Flavor.SPICY, Flavor.SOUR),
  BRAVE(PokemonStat.ATTACK, PokemonStat.SPEED, Flavor.SPICY, Flavor.SWEET),
  ADAMANT(PokemonStat.ATTACK, PokemonStat.SP_ATTACK, Flavor.SPICY, Flavor.DRY),
  NAUGHTY(PokemonStat.ATTACK, PokemonStat.SP_DEFENSE, Flavor.SPICY, Flavor.BITTER),
  BOLD(PokemonStat.DEFENSE, PokemonStat.ATTACK, Flavor.SOUR, Flavor.SPICY),
  DOCILE(PokemonStat.DEFENSE, PokemonStat.DEFENSE, Flavor.SOUR, Flavor.SOUR),
  RELAXED(PokemonStat.DEFENSE, PokemonStat.SPEED, Flavor.SOUR, Flavor.SWEET),
  IMPISH(PokemonStat.DEFENSE, PokemonStat.SP_ATTACK, Flavor.SOUR, Flavor.DRY),
  LAX(PokemonStat.DEFENSE, PokemonStat.SP_DEFENSE, Flavor.SOUR, Flavor.BITTER),
  TIMID(PokemonStat.SPEED, PokemonStat.ATTACK, Flavor.SWEET, Flavor.SPICY),
  HASTY(PokemonStat.SPEED, PokemonStat.DEFENSE, Flavor.SWEET, Flavor.SOUR),
  SERIOUS(PokemonStat.SPEED, PokemonStat.SPEED, Flavor.SWEET, Flavor.SWEET),
  JOLLY(PokemonStat.SPEED, PokemonStat.SP_ATTACK, Flavor.SWEET, Flavor.DRY),
  NAIVE(PokemonStat.SPEED, PokemonStat.SP_DEFENSE, Flavor.SWEET, Flavor.BITTER),
  MODEST(PokemonStat.SP_ATTACK, PokemonStat.ATTACK, Flavor.DRY, Flavor.SPICY),
  MILD(PokemonStat.SP_ATTACK, PokemonStat.DEFENSE, Flavor.DRY, Flavor.SOUR),
  QUIET(PokemonStat.SP_ATTACK, PokemonStat.SPEED, Flavor.DRY, Flavor.SWEET),
  BASHFUL(PokemonStat.SP_ATTACK, PokemonStat.SP_ATTACK, Flavor.DRY, Flavor.DRY),
  RASH(PokemonStat.SP_ATTACK, PokemonStat.SP_DEFENSE, Flavor.DRY, Flavor.BITTER),
  CALM(PokemonStat.SP_DEFENSE, PokemonStat.ATTACK, Flavor.BITTER, Flavor.SPICY),
  GENTLE(PokemonStat.SP_DEFENSE, PokemonStat.DEFENSE, Flavor.BITTER, Flavor.SOUR),
  SASSY(PokemonStat.SP_DEFENSE, PokemonStat.SPEED, Flavor.BITTER, Flavor.SWEET),
  CAREFUL(PokemonStat.SP_DEFENSE, PokemonStat.SP_ATTACK, Flavor.BITTER, Flavor.DRY),
  QUIRKY(PokemonStat.SP_DEFENSE, PokemonStat.SP_DEFENSE, Flavor.BITTER, Flavor.BITTER)
}

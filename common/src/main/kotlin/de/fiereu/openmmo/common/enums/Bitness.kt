package de.fiereu.openmmo.common.enums

enum class Bitness {
  _32,
  _64,
  _128;

  companion object {
    fun from(ordinal: Int): Bitness {
      return entries.firstOrNull { it.ordinal == ordinal }
          ?: throw IllegalArgumentException("Unknown bitness: $ordinal")
    }
  }
}

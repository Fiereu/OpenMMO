package de.fiereu.openmmo.common.enums

enum class Arch {
  X86,
  ARM,
  RISCV,
  LOONGARCH;

  companion object {
    fun from(ordinal: Int): Arch {
      return entries.firstOrNull { it.ordinal == ordinal }
          ?: throw IllegalArgumentException("Unknown arch: $ordinal")
    }
  }
}

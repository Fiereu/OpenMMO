package de.fiereu.openmmo.protocols.game.packets.codecs

import de.fiereu.openmmo.common.enums.SkinSlot
import io.netty.buffer.ByteBuf
import java.util.EnumMap
import kotlin.enums.EnumEntries

data class Skin(val slot: SkinSlot, val type: UShort?, val color: UByte?)

class SkinSet() : EnumMap<SkinSlot, Skin>(SkinSlot::class.java) {
  fun put(skin: Skin) {
    this[skin.slot] = skin
  }

  override fun put(key: SkinSlot, value: Skin): Skin? {
    require(key == value.slot) { "Slot mismatch: $key != ${value.slot}" }
    return super.put(key, value)
  }
}

fun ByteBuf.writeSkinsLE(
    skinSet: SkinSet,
    slots: EnumEntries<SkinSlot> = SkinSlot.entries,
    writeByte: Boolean = true
) = apply {
  if (writeByte) {
    writeByte(0)
  }
  val mask = slots.fold(0) { acc, slot -> acc or (1 shl slot.ordinal) }
  writeShortLE(mask)

  slots.forEach { slot ->
    val skin = skinSet[slot] ?: Skin(slot, 0u, 0u)
    val type = skin.type ?: 0x3FFFu
    val color = skin.color ?: 0x3Fu
    require(type <= 0x3FFFu) { "Skin type too large: ${skin.type}" }
    require(color <= 0x3Fu) { "Skin color too large: ${skin.color}" }

    val compressed = (type and 0x3FFFu).toInt() or ((color and 0x3Fu).toInt() shl 10)

    writeShortLE(compressed)
  }
}

fun ByteBuf.readSkinsLE(
    slots: EnumEntries<SkinSlot>,
    readByte: Boolean = true,
): SkinSet {
  if (readByte) {
    readByte()
  }
  val mask = readUnsignedShortLE()
  val skinSet = SkinSet()

  slots.forEach { slot ->
    if ((mask and (1 shl slot.ordinal)) != 0) {
      val compressed = readUnsignedShortLE()
      val type = (compressed and 0x3FFF).toUShort()
      val color = ((compressed shr 10) and 0x3F).toUByte()
      skinSet.put(Skin(slot, type, color))
    }
  }

  return skinSet
}

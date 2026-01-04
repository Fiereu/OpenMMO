package de.fiereu.openmmo.protocols.game.packets.codecs

import de.fiereu.openmmo.common.CharacterInfo
import de.fiereu.openmmo.protocols.readEpochSeconds
import de.fiereu.openmmo.protocols.readUtf16LE
import de.fiereu.openmmo.protocols.writeEpochSecond
import de.fiereu.openmmo.protocols.writeUtf16LE
import io.netty.buffer.ByteBuf

fun ByteBuf.writeCharacterInfoLE(useLong: Boolean, characterInfo: CharacterInfo) = apply {
  writeLongLE(characterInfo.id)
  writeUtf16LE(characterInfo.name)
  writeUtf16LE("")
  writeIntLE(characterInfo.userId)
  writeByte(characterInfo.rivalSex.toInt())
  writeEpochSecond(characterInfo.lastLogin)
  if (useLong) {
    writeLongLE(0)
  }
  writeEpochSecond(characterInfo.createdAt)
  writeIntLE(0) // unused
  writeByte(0) // unused
  writeIntLE(0)
  writeIntLE(characterInfo.money)
  writeShortLE(0)
  writeIntLE(0)
  writeByte(characterInfo.permissions)
  writeByte(0) // unused
  writeByte(0)
  writeIntLE(0)
  repeat(8) {
    writeByte(0) // unused
  }
  writeShortLE(characterInfo.remainingSafariSteps.toInt())
  writeByte(characterInfo.remainingSafariBalls.toInt())
  writeIntLE(0)
  writeByte(characterInfo.pcExtraSlots.toInt())
  writeByte(characterInfo.battleBoxExtraSlots.toInt())
  writeByte(characterInfo.templateAmount.toInt())
  writeByte(0) // unused
  writeByte(0) // unused
  writeByte(0) // unused
  writeByte(characterInfo.positionRegionId.toInt())
  writeByte(characterInfo.positionBankId.toInt())
  writeByte(characterInfo.positionMapId.toInt())
  writeByte(0) // unused
  writeShortLE(characterInfo.positionX.toInt())
  writeShortLE(characterInfo.positionY.toInt())
  writeByte(0) // unused
  writeByte(0) // unused
  writeShortLE(characterInfo.repelLeft.toInt())
  writeShortLE(characterInfo.repelItemId.toInt())
  writeByte(0) // 100
  writeShortLE(characterInfo.lureItemId.toInt())
  writeShortLE(characterInfo.lureLeft.toInt())
  val bytes = ByteArray(0)
  writeByte(bytes.size)
  writeBytes(bytes)
}

fun ByteBuf.readCharacterInfoLE(useLong: Boolean): CharacterInfo = let {
  val id = readLongLE()
  val name = readUtf16LE()
  val namePrefix = readUtf16LE()
  val userId = readIntLE()
  val rivalSex = readByte()
  val lastLogin = readEpochSeconds()
  if (useLong) {
    readLongLE()
  }
  val createdAt = readEpochSeconds()
  readIntLE() // unused
  readByte() // unused
  readIntLE()
  val money = readIntLE()
  readShortLE()
  readIntLE()
  val permissions = readByte().toInt()
  readByte() // unused
  readByte()
  readIntLE()
  repeat(8) {
    readByte() // unused
  }
  val remainingSafariSteps = readShortLE()
  val remainingSafariBalls = readByte()
  readIntLE()
  val pcExtraSlots = readByte()
  val battleBoxExtraSlots = readByte()
  val templateAmount = readByte()
  readByte() // unused
  readByte() // unused
  readByte() // unused
  val positionRegionId = readByte()
  val positionBankId = readByte()
  val positionMapId = readByte()
  readByte() // unused
  val positionX = readShortLE()
  val positionY = readShortLE()
  readByte() // unused
  readByte() // unused
  val repelLeft = readShortLE()
  val repelItemId = readShortLE()
  readByte()
  val lureItemId = readShortLE()
  val lureLeft = readShortLE()
  val arraySize = readByte().toInt()
  if (arraySize > 0) {
    val bytes = ByteArray(arraySize)
    readBytes(bytes)
  }
  return CharacterInfo(
      id,
      name,
      namePrefix,
      userId,
      rivalSex,
      lastLogin,
      createdAt,
      money,
      permissions,
      remainingSafariSteps,
      remainingSafariBalls,
      pcExtraSlots,
      battleBoxExtraSlots,
      templateAmount,
      positionRegionId,
      positionBankId,
      positionMapId,
      positionX,
      positionY,
      repelLeft,
      repelItemId,
      lureLeft,
      lureItemId)
}

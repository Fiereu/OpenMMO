package de.fiereu.openmmo.protocols.tls.util

import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.Signature
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec

private const val uncompressedPointIndicator: Byte = 0x04

private val keyFactory: KeyFactory = KeyFactory.getInstance("EC")

private val ecParameterSpec: ECParameterSpec by lazy {
  val algorithmParameters = AlgorithmParameters.getInstance("EC")
  algorithmParameters.init(ECGenParameterSpec("secp256r1"))
  algorithmParameters.getParameterSpec(ECParameterSpec::class.java)
}

fun ECPublicKey.toUncompressedPoint(): ByteArray {
  val key = this
  val point = key.w
  val keyLength = (ecParameterSpec.order.bitLength() + Byte.SIZE_BITS - 1) / Byte.SIZE_BITS

  var offset = 0
  val data = ByteArray(1 + 2 * keyLength)
  data[offset++] = uncompressedPointIndicator

  val x = point.affineX.toByteArray()
  val y = point.affineY.toByteArray()

  when {
    x.size <= keyLength -> {
      System.arraycopy(x, 0, data, offset + keyLength - x.size, x.size)
    }
    x.size == keyLength + 1 && x[0] == 0.toByte() -> {
      System.arraycopy(x, 1, data, offset, keyLength)
    }
    else -> {
      throw IllegalStateException("x value is too large")
    }
  }

  offset += keyLength
  when {
    y.size <= keyLength -> {
      System.arraycopy(y, 0, data, offset + keyLength - y.size, y.size)
    }
    y.size == keyLength + 1 && y[0] == 0.toByte() -> {
      System.arraycopy(y, 1, data, offset, keyLength)
    }
    else -> {
      throw IllegalStateException("y value is too large")
    }
  }

  return data
}

fun ByteArray.toECPublicKey(): ECPublicKey {
  if (this[0] != uncompressedPointIndicator) {
    throw IllegalArgumentException("Invalid uncompressed point indicator")
  }

  val keyLength = (ecParameterSpec.order.bitLength() + Byte.SIZE_BITS - 1) / Byte.SIZE_BITS
  if (this.size != 1 + 2 * keyLength) {
    throw IllegalArgumentException("Invalid data length")
  }

  var offset = 1
  val x = BigInteger(1, this.copyOfRange(offset, offset + keyLength))
  offset += keyLength
  val y = BigInteger(1, this.copyOfRange(offset, offset + keyLength))
  val point = ECPoint(x, y)

  return keyFactory.generatePublic(ECPublicKeySpec(point, ecParameterSpec)) as ECPublicKey
}

fun ECPrivateKey.sign(data: ByteArray): ByteArray {
  val signature = Signature.getInstance("SHA256withECDSA")
  signature.initSign(this)
  signature.update(data)
  return signature.sign()
}

fun ECPublicKey.verify(data: ByteArray, signatureBytes: ByteArray): Boolean {
  val signature = Signature.getInstance("SHA256withECDSA")
  signature.initVerify(this)
  signature.update(data)
  return signature.verify(signatureBytes)
}

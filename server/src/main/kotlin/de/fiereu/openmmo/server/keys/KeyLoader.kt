package de.fiereu.openmmo.server.keys

import io.github.oshai.kotlinlogging.KotlinLogging
import org.bouncycastle.util.io.pem.PemReader
import java.io.InputStream
import java.security.interfaces.ECPublicKey
import java.security.spec.X509EncodedKeySpec

val log = KotlinLogging.logger {}

object KeyLoader {
  fun loadPemECPublicKey(inputStream: InputStream): ECPublicKey {
    PemReader(inputStream.reader()).use { pemReader ->
      val pemObject = pemReader.readPemObject()
      val keySpec = X509EncodedKeySpec(pemObject.content)
      val keyFactory = java.security.KeyFactory.getInstance("EC")
      return keyFactory.generatePublic(keySpec) as ECPublicKey
    }
  }

  fun loadPemECPrivateKey(inputStream: InputStream): java.security.interfaces.ECPrivateKey {
    PemReader(inputStream.reader()).use { pemReader ->
      val pemObject = pemReader.readPemObject()
      val keySpec = java.security.spec.PKCS8EncodedKeySpec(pemObject.content)
      val keyFactory = java.security.KeyFactory.getInstance("EC")
      return keyFactory.generatePrivate(keySpec) as java.security.interfaces.ECPrivateKey
    }
  }
}
package de.fiereu.openmmo.common.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun ByteArray.gzipCompress(): ByteArray {
  val baos = ByteArrayOutputStream()
  val gzip = GZIPOutputStream(baos)
  gzip.write(this)
  gzip.close()
  return baos.toByteArray()
}

fun ByteArray.gzipDecompress(): ByteArray =
    GZIPInputStream(ByteArrayInputStream(this)).buffered().readBytes()

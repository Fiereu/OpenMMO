package de.fiereu.openmmo.patcher

import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * A class file transformer that patches a given class files.
 *
 * @property stringPatches The list of string patches to apply.
 */
class PatchingClassFileTransformer(private val stringPatches: List<StringPatcher.Patch>) :
    ClassFileTransformer {

  override fun transform(
      loader: ClassLoader?,
      className: String,
      classBeingRedefined: Class<*>?,
      protectionDomain: ProtectionDomain,
      classfileBuffer: ByteArray
  ): ByteArray? {
    return transform(classfileBuffer)
  }

  override fun transform(
      module: Module,
      loader: ClassLoader?,
      className: String,
      classBeingRedefined: Class<*>?,
      protectionDomain: ProtectionDomain,
      classfileBuffer: ByteArray
  ): ByteArray? {
    return transform(classfileBuffer)
  }

  private fun transform(classFile: ByteArray): ByteArray {
    try {
      val reader = ClassReader(classFile)
      val writer = ClassWriter(null, 0)
      val stringPatcher = StringPatcher(writer, stringPatches)
      reader.accept(stringPatcher, 0)
      return writer.toByteArray()
    } catch (e: Exception) {
      Agent.log { "Failed to transform class: ${e.message}" }
      e.printStackTrace()
      return classFile
    }
  }
}

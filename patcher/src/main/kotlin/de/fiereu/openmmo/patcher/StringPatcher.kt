package de.fiereu.openmmo.patcher

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * A class visitor that patches string literals in methods.
 *
 * @property classVisitor The class visitor to delegate to.
 * @property patches The list of string patches to apply.
 */
class StringPatcher(classVisitor: ClassVisitor, private val patches: List<Patch>) :
    ClassVisitor(Opcodes.ASM9, classVisitor) {

  /**
   * Represents a string patch that replaces an original string with a replacement string.
   *
   * @param name The name of the patch, used for logging and identification.
   * @param original The original string to be replaced.
   * @param replacement The string that will replace the original string.
   */
  data class Patch(
      val name: String = "UnknownPatch",
      val original: String,
      val replacement: String
  )

  private fun applyPatches(value: String): String {
    val patches = patches.filter { it.original == value }
    var newValue: String = value
    patches.forEach { patch ->
      Agent.log { "Applying patch: ${patch.name} - Replacing '$value' with '${patch.replacement}'" }
      newValue = patch.replacement
    }
    return newValue
  }

  override fun visitField(
      access: Int,
      name: String,
      descriptor: String,
      signature: String?,
      value: Any?
  ): FieldVisitor? {
    if (value is String) {
      return super.visitField(access, name, descriptor, signature, applyPatches(value))
    }
    return super.visitField(access, name, descriptor, signature, value)
  }

  override fun visitMethod(
      access: Int,
      name: String,
      descriptor: String,
      signature: String?,
      exceptions: Array<out String>?
  ): MethodVisitor {
    return object :
        MethodVisitor(
            Opcodes.ASM9, super.visitMethod(access, name, descriptor, signature, exceptions)) {
      override fun visitLdcInsn(value: Any?) {
        if (value is String) {
          super.visitLdcInsn(applyPatches(value))
        } else {
          super.visitLdcInsn(value)
        }
      }
    }
  }
}

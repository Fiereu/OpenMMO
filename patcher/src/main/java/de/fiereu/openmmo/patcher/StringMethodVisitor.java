package de.fiereu.openmmo.patcher;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class StringMethodVisitor extends MethodVisitor {

  private final String toReplace;
  private final String replaceWith;

  public StringMethodVisitor(MethodVisitor mv, String toReplace, String replaceWith) {
    super(Opcodes.ASM5, mv);
    this.toReplace = toReplace;
    this.replaceWith = replaceWith;
  }

  @Override
  public void visitLdcInsn(Object cst) {
    if (cst instanceof String && cst.equals(toReplace)) {
      System.out.println("Replacing constant: " + cst + " -> " + replaceWith);
      super.visitLdcInsn(replaceWith);
    } else {
      super.visitLdcInsn(cst);
    }
  }
}

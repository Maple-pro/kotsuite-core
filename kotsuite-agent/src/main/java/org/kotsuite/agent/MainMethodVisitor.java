package org.kotsuite.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MainMethodVisitor extends MethodVisitor {
    private final MethodVisitor methodVisitor;
    private final String className;
    private final String methodName;

    protected MainMethodVisitor(MethodVisitor methodVisitor, String className, String methodName) {
        super(Opcodes.ASM7, methodVisitor);
        this.methodVisitor = methodVisitor;
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public void visitCode() {
        super.visitCode();

//        System.out.println("Start deal with main method");

        methodVisitor.visitTypeInsn(Opcodes.NEW, className);
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL, className, "<init>", "()V", false
        );

        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, className, methodName, "()V", false
        );

//        System.out.println("Add Complete!");
    }
}

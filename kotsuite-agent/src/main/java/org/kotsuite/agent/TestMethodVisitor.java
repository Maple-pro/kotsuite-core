package org.kotsuite.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class TestMethodVisitor extends MethodVisitor {
    AgentOptions options;
    MethodVisitor methodVisitor;

    protected TestMethodVisitor(MethodVisitor methodVisitor, AgentOptions options) {
        super(Opcodes.ASM7, methodVisitor);
        this.options = options;
        this.methodVisitor = methodVisitor;
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {
            // Insert bytecode to store the return value in a local variable
            mv.visitVarInsn(Opcodes.ASTORE, 1); // Store the return value in local variable 1
            mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "ReturnValueHandler",
                    "handleReturnValue",
                    "(Ljava/lang/Object;Ljava/lang/String;)V",
                    false
            );
        }
        super.visitInsn(opcode);
    }
}

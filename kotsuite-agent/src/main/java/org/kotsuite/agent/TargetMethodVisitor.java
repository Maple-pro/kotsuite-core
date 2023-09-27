package org.kotsuite.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class TargetMethodVisitor extends MethodVisitor {
    AgentOptions options;
    MethodVisitor methodVisitor;

    protected TargetMethodVisitor(MethodVisitor methodVisitor, AgentOptions options) {
        super(Opcodes.ASM7, methodVisitor);
        this.options = options;
        this.methodVisitor = methodVisitor;
    }

    /**
     * @param opcode the opcode of the instruction to be visited. This opcode is either NOP,
     */
    @Override
    public void visitInsn(int opcode) {
//        System.out.println("opcode: " + opcode);
        if ((opcode == Opcodes.IRETURN) || (opcode == Opcodes.LRETURN) || (opcode == Opcodes.FRETURN)
                || (opcode == Opcodes.DRETURN) || (opcode == Opcodes.ARETURN) || (opcode == Opcodes.RETURN)) {
            // Load the method return type onto the stack based on the return type
            switch (opcode) {
                case Opcodes.ARETURN: // return reference type
                    mv.visitInsn(Opcodes.DUP);
//                    // Print it to Console
//                    if (returnType.equals("Ljava/lang/String;")) {
//                        mv.visitInsn(Opcodes.DUP);
//                        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//                        mv.visitInsn(Opcodes.SWAP);
//                        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//                    }
                    break;

                case Opcodes.IRETURN: // return int type
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
//                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//                    mv.visitInsn(Opcodes.SWAP);
//                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
                    break;

                case Opcodes.LRETURN: // return long type
                    mv.visitInsn(Opcodes.DUP2);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
//                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//                    mv.visitInsn(Opcodes.DUP_X2);
//                    mv.visitInsn(Opcodes.POP2);
//                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
                    break;

                case Opcodes.FRETURN: // return float type
                    mv.visitInsn(Opcodes.DUP);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
//                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//                    mv.visitInsn(Opcodes.SWAP);
//                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
                    break;

                case Opcodes.DRETURN: // return double type
                    mv.visitInsn(Opcodes.DUP2);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
//                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//                    mv.visitInsn(Opcodes.DUP2_X2);
//                    mv.visitInsn(Opcodes.POP2);
//                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V", false);
                    break;

                default: // return void
                    break;
            }
            if (opcode != Opcodes.RETURN) {
                printToFile(options.getOutputFile(), options.getTestClass(), options.getTestMethod());
            }

            mv.visitInsn(opcode);
        } else {
            super.visitInsn(opcode);
        }
    }

    private void printString(String msg) {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(msg);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    private void printToFile(String filename, String className, String methodName) {
        // Insert bytecode to store the return value in a local variable
        //            mv.visitVarInsn(Opcodes.ASTORE, 1); // Store the return value in local variable 1
        mv.visitLdcInsn(filename);
        mv.visitLdcInsn(className);
        mv.visitLdcInsn(methodName);
        mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "org/kotsuite/agent/runtime/ReturnValueHandler",
                "handleReturnValue",
                "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                false
        );
    }
}

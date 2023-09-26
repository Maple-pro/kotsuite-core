package org.kotsuite.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MainClassVisitor extends ClassVisitor {
    AgentOptions options;
    public MainClassVisitor(ClassVisitor classVisitor, AgentOptions options) {
        super(Opcodes.ASM7, classVisitor);
        this.options = options;
    }

    @Override
    public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
    ) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("main")) {
            return new MainMethodVisitor(methodVisitor, options);
        }
        return methodVisitor;
    }
}

package org.kotsuite.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class TestClassVisitor extends ClassVisitor {
    AgentOptions options;

    TestClassVisitor(ClassVisitor classVisitor, AgentOptions options) {
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
        if (options.getTestMethod() != null && name.equals(options.getTestMethod())) {
            return new TestMethodVisitor(methodVisitor, options);
        }
        return methodVisitor;
    }
}

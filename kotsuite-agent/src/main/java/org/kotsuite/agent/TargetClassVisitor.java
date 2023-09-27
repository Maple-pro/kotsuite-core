package org.kotsuite.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class TargetClassVisitor extends ClassVisitor {
    AgentOptions options;

    TargetClassVisitor(ClassVisitor classVisitor, AgentOptions options) {
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
        if (name.equals(options.getTargetMethod()) && descriptor.equals(options.getTargetMethodDesc())) {
//            System.out.println("Found test method: " + options.getTestMethod());
            return new TargetMethodVisitor(methodVisitor, options);
        }
        return methodVisitor;
    }
}

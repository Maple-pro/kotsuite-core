package org.kotsuite.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

public class MainClassVisitor extends ClassVisitor {

    private final String className;
    private final String methodName;

    protected MainClassVisitor(ClassWriter classWriter, String className, String methodName) {
        super(Opcodes.ASM7, classWriter);
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public MethodVisitor visitMethod(
            int access,
            String name,
            String descriptor,
            String signature,
            String[] exceptions
    ) {
        System.out.println("method: " + name);
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("main")) {
            return new MainMethodVisitor(methodVisitor, className, methodName);
        }
        return methodVisitor;
    }

}

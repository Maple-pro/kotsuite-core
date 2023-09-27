package org.kotsuite.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainMethodVisitor extends MethodVisitor {
    AgentOptions options;
    private final MethodVisitor methodVisitor;

    protected MainMethodVisitor(MethodVisitor methodVisitor, AgentOptions options) {
        super(Opcodes.ASM7, methodVisitor);
        this.options = options;
        this.methodVisitor = methodVisitor;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        String className = options.getTestClass();
        String methodName = options.getTestMethod();

        try {
            if (methodName.equals("*")) { // invoke all declared methods in target class
                Class<?> targetClass = Class.forName(className);
                Method[] methods = targetClass.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().equals("$jacocoInit")) continue;
                    addMethodCall(options.getASMTestClass(), method.getName(), Type.getMethodDescriptor(method));
                }
            } else { // invoke the given method in target class
              addMethodCall(options.getASMTestClass(), options.getTestMethod(), options.getTestMethodDesc());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + className);
            throw new RuntimeException(e);
        }
    }

    private void addMethodCall(String asmClassName, String methodName, String methodDesc) {
        methodVisitor.visitTypeInsn(Opcodes.NEW, asmClassName);
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL, asmClassName, "<init>", "()V", false
        );
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, asmClassName, methodName, methodDesc, false
        );
    }
}

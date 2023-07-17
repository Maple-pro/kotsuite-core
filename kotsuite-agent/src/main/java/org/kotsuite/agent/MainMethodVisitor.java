package org.kotsuite.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;

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

        if (methodName.equals("*")) { // invoke all declared methods in target class
            try {
                Class<?> targetClass = Class.forName(className.replace('/', '.'));
                Method[] methods = targetClass.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().equals("$jacocoInit")) continue;
                    System.out.println("Inserting method: " + method.getName());
                    addMethodCall(method.getName());
                }

            } catch (ClassNotFoundException e) {
                System.out.println("Class not found: " + className);
                throw new RuntimeException(e);
            }
        } else { // invoke the given method in target class
            addMethodCall(methodName);
        }
    }

    private void addMethodCall(String method) {
        methodVisitor.visitTypeInsn(Opcodes.NEW, className);
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL, className, "<init>", "()V", false
        );
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, className, method, "()V", false
        );
    }
}

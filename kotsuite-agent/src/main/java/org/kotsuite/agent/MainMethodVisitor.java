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
            Class<?> targetClass = Class.forName(className.replace('/', '.'));

            if (methodName.equals("*")) { // invoke all declared methods in target class
                Method[] methods = targetClass.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().equals("$jacocoInit")) continue;
//                    System.out.println("Inserting method: " + method.getName());
                    addMethodCall(targetClass, method);
                }
            } else { // invoke the given method in target class
                List<Method> targetMethods = getMethodsByName(targetClass, methodName);
                if (targetMethods.isEmpty()) {
                    System.err.println("Method not found: " + methodName);
                    throw new RuntimeException();
                } else {
                    addMethodCall(targetClass,targetMethods.get(0));
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + className);
            throw new RuntimeException(e);
        }
    }

    private void addMethodCall(Class<?> clazz, Method method) {
        String className = clazz.getName();
        String methodName = method.getName();
        String methodDesc = Type.getMethodDescriptor(method);

        methodVisitor.visitTypeInsn(Opcodes.NEW, className);
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL, className, "<init>", "()V", false
        );
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, className, methodName, methodDesc, false
        );
    }

    private List<Method> getMethodsByName(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> targetMethods = new ArrayList<>();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                targetMethods.add(method);
            }
        }

        return targetMethods;
    }
}

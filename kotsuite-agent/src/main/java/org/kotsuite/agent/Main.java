package org.kotsuite.agent;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Main {
    static String className;
    static String methodName;

    public static void premain(String agentArgs, Instrumentation inst) {

        String[] args = agentArgs.split("\\.");
        if (args.length != 2) {
            return;
        }

        Main.className = args[0];
        Main.methodName = args[1];

        try {
            inst.addTransformer(new ClassCalleeTransformer());
        } catch (Exception exception) {
            System.out.println("error: " + exception.getLocalizedMessage());
        }

    }

    public static void agentmain(String agentArgs, Instrumentation inst) {

    }

    private static class ClassCalleeTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain,
                byte[] classfileBuffer
        ) throws IllegalClassFormatException {

            if (className.equals("KotMain")) {

                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(
                        reader,
                        ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
                );
                reader.accept(new MyMainClassVisitor(writer), ClassReader.EXPAND_FRAMES);


                return writer.toByteArray();
            }

            return null;
        }
    }

    private static class MyMainClassVisitor extends ClassVisitor {
        public MyMainClassVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM7, classVisitor);
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
                return new MainMethodVisitor(methodVisitor, Main.className, Main.methodName);
            }
            return methodVisitor;
        }
    }

}

package org.kotsuite.agent;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Main {
    static String className;
    static String methodName;

    public static void premain(String agentArgs, Instrumentation inst) {
//        System.out.println("start kotsuite agent");

        int splitIndex = agentArgs.lastIndexOf('.');
        className = agentArgs.substring(0, splitIndex).replace('.', '/');
        methodName = agentArgs.substring(splitIndex + 1);

//        System.out.println("Add method: " + className + "." + methodName);

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
        ) {

            if (className.equals("KotMain")) {

//                System.out.println("Start deal with KotMain class");

                // First visitor: add method call statements
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(
                        reader,
                        ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
                );
                reader.accept(new MyMainClassVisitor(writer), ClassReader.EXPAND_FRAMES);

                // Second visitor: print method
//                ClassReader reader2 = new ClassReader(writer.toByteArray());
//                ClassWriter writer2 = new ClassWriter(
//                        reader2,
//                        ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
//                );
//                reader.accept(new PrinterClassVisitor(writer2), ClassReader.EXPAND_FRAMES);

//                System.out.println("success!");

//                return writer2.toByteArray();
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

    private static class PrinterClassVisitor extends ClassVisitor {
        static ClassVisitor cv;
        PrinterClassVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM7, classVisitor);
            cv = classVisitor;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            System.out.println("Start printing method: " + name);
            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
            Printer p = new Textifier(Opcodes.ASM5) {
                @Override
                public void visitMethodEnd() {
                    System.out.println("Printing!");
                    PrintWriter printWriter = new PrintWriter(System.out);
                    print(printWriter);
                    printWriter.flush();
                }
            };
            return new TraceMethodVisitor(mv, p);
        }
    }

}

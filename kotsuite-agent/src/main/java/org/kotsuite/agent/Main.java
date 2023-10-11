package org.kotsuite.agent;

import org.objectweb.asm.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Main {
    static AgentOptions options;

    /**
     *
     * @param agentArgs the target class and method, e.g., <code>ExampleTest.foo</code> or <code>ExampleTest.*</code>
     */
    public static void premain(String agentArgs, Instrumentation inst) {
//        System.out.println("premain!!!");
        options = new AgentOptions(agentArgs);
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
//            System.out.println("Transforming class: " + className);

            // First visitor: add method call statements
//            if (options.isInsertCall() && className.equals(options.getASMMainClass())) {
//                ClassReader reader = new ClassReader(classfileBuffer);
//                ClassWriter writer = new ClassWriter(
//                        reader,
//                        ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
//                );
//                reader.accept(new MainClassVisitor(writer, options), ClassReader.EXPAND_FRAMES);
//
//                return writer.toByteArray();
//            }

            // Second visitor: get return value of the test case
            if (options.isCollectAssert() && className.equals(options.getASMTargetClass())) {
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(
                        reader,
                        ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
                );
                reader.accept(new TargetClassVisitor(writer, options), ClassReader.EXPAND_FRAMES);

                return writer.toByteArray();
            }

            return null;
        }
    }
}

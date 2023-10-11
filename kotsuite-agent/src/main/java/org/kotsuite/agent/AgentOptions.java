package org.kotsuite.agent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to create and parse options for the agent.
 * Options are represented as a comma separated list of key=value pairs, e.g.,
 *
 * <pre>
 *   key1=value1,key2=value2
 * </pre>
 */
public class AgentOptions {
//    public static final String INSERT_CALL = "insertCall"; // 是否在main method中插入test method方法调用, true | false

    public static final String COLLECT_ASSERT = "collectAssert"; // 是否收集target method的返回结果, true | false

    public static final String OUTPUT_FILE = "outputFile"; // 收集target method的返回结果的输出文件路径

//    public static final String MAIN_CLASS = "mainClass"; // main class

    public static final String TEST_CLASS = "testClass"; // 需要插入的测试类

    public static final String TEST_METHOD = "testMethod"; // 需要插入的测试方法名

//    public static final String TEST_METHOD_DESC = "testMethodDesc"; // 需要插入的测试方法描述符

    public static final String TARGET_CLASS = "targetClass"; // 测试类对应的待测类

    public static final String TARGET_METHOD = "targetMethod"; // 测试用例对应的待测方法（需要收集返回值用于 Assert）

    public static final String TARGET_METHOD_DESC = "targetMethodDesc"; // 测试用例对应的待测方法的描述符

    private static final Collection<String> VALID_OPTIONS = Arrays.asList(
//            INSERT_CALL,
            COLLECT_ASSERT,
            OUTPUT_FILE,
//            MAIN_CLASS,
            TEST_CLASS,
            TEST_METHOD,
//            TEST_METHOD_DESC,
            TARGET_CLASS,
            TARGET_METHOD,
            TARGET_METHOD_DESC
    );

    private final Map<String, String> options;

    public AgentOptions() {
        this.options = new HashMap<>();
    }

    public AgentOptions(final String optionStr) {
        this();
        if (optionStr != null && !optionStr.isEmpty()) {
            for (String entry : optionStr.split(",")) {
                String[] parts = entry.split("=");
                String key = parts[0].trim();
                String value = "";
                if (parts.length == 2) {
                    value = parts[1].trim();
                }
                if (!VALID_OPTIONS.contains(key)) {
                    throw new IllegalArgumentException("Invalid option: " + key);
                }
                options.put(key, value);
            }
        }
    }

//    public boolean isInsertCall() {
//        return options.get(INSERT_CALL).equals("true");
//    }

    public boolean isCollectAssert() {
        return options.get(COLLECT_ASSERT).equals("true");
    }

    public String getOutputFile() {
        if (options.get(OUTPUT_FILE) == null) {
            return "asserts.txt";
        } else {
            return options.get(OUTPUT_FILE);
        }
    }

//    public String getMainClass() {
//        return options.get(MAIN_CLASS);
//    }

//    public String getASMMainClass() {
//        return options.get(MAIN_CLASS).replace('.', '/');
//    }

    public String getTestClass() {
        return options.get(TEST_CLASS);
    }

    public String getASMTestClass() {
        if (!options.containsKey(TEST_CLASS)) {
            return "";
        }
        return options.get(TEST_CLASS).replace('.', '/');
    }

    public String getTestMethod() {
        return options.get(TEST_METHOD);
    }

//    public String getTestMethodDesc() {
//        return options.get(TEST_METHOD_DESC);
//    }

    public String getTargetClass() {
        return options.get(TARGET_CLASS);
    }

    public String getASMTargetClass() {
        if (!options.containsKey(TARGET_CLASS)) {
            return "";
        }
        return options.get(TARGET_CLASS).replace('.', '/');
    }

    public String getTargetMethod() {
        return options.get(TARGET_METHOD);
    }

    public String getTargetMethodDesc() {
        return options.get(TARGET_METHOD_DESC);
    }

}

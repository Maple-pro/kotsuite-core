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
    public static final String MAIN_CLASS = "mainclass";

    public static final String TEST_CLASS = "testclass";

    public static final String TEST_METHOD = "testmethod";

    private static final Collection<String> VALID_OPTIONS = Arrays.asList(
            MAIN_CLASS,
            TEST_CLASS,
            TEST_METHOD
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
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid option: " + entry);
                }

                String key = parts[0].trim();
                String value = parts[1].trim();
                if (!VALID_OPTIONS.contains(key)) {
                    throw new IllegalArgumentException("Invalid option: " + key);
                }
                options.put(key, value);
            }
        }
    }

    public String getMainClass() {
        return options.get(MAIN_CLASS);
    }

    public String getTestClass() {
        return options.get(TEST_CLASS);
    }

    public String getTestMethod() {
        return options.get(TEST_METHOD);
    }
}

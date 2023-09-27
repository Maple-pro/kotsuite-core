package org.kotsuite.agent.runtime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * It will be called by the instrumented code to handle the return value of the target method.
 */
public class ReturnValueHandler {
    public static void handleReturnValue(Object value, String filename, String testClass, String testMethod) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            Gson gson = new GsonBuilder().create();

            // Create a map to hold the data
            Map<String, String> data = new HashMap<>();
            data.put("class", testClass);
            data.put("method", testMethod);
            data.put("assert_type", value.getClass().toString());
            data.put("assert_value", value.toString()); // TODO: deal with more reference types
            String json = gson.toJson(data);
            writer.write(json);

            writer.newLine();
        } catch (IOException exception) {
            System.err.println("Error: " + exception.getLocalizedMessage());
        }
    }
}

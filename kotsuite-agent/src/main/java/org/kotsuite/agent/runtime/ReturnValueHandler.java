package org.kotsuite.agent.runtime;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ReturnValueHandler {
    public static void handleReturnValue(Object value, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("<returnValue>");
            writer.write(value.toString());
            writer.write("</returnValue>");
            writer.newLine();
        } catch (IOException exception) {
            System.err.println("Error: " + exception.getLocalizedMessage());
        }
    }
}

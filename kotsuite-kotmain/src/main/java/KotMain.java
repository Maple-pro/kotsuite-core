import org.apache.commons.cli.*;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.Arrays;

public class KotMain {

    public static void main(String[] args) {
        Options options = new Options();
//        options.addOption("c", "class", true, "test class name");
        options.addOption("m", "method", true, "test method name");
        options.addOption(Option.builder("c")
                .longOpt("class")
                .hasArg()
                .desc("test class name")
                .valueSeparator(',')
                .build());

        DefaultParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Command line parameters error");
            System.exit(1);
            return;
        }

        String[] classes = cmd.getOptionValue("c").split(",");
        String method = cmd.getOptionValue("m");

        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));

        if (method.equals("*")) { // Run all test methods in test classes
            Class<?>[] testClasses = Arrays.stream(classes).map(KotMain::getTestClassForName).toArray(Class<?>[]::new);
            Request request = Request.classes(testClasses);
            Result result = junit.run(request);

            // Print the results
            for (Failure failure : result.getFailures()) {
                System.err.println(failure.toString());
            }

            System.out.println("Test successful: " + result.wasSuccessful());
        } else { // Run single test method
            if (classes.length != 1) {
                System.err.println("Only one test class is allowed when running a single test method");
                System.exit(1);
                return;
            }

            // Run the specified JUnit test case
            Request request = Request.method(getTestClassForName(classes[0]), method);
            Result result = junit.run(request);

            // Print the results
            for (Failure failure : result.getFailures()) {
                System.err.println(failure.toString());
            }

            System.out.println("Test successful: " + result.wasSuccessful());
        }
    }

    private static Class<?> getTestClassForName(String testCaseName) {
        try {
            return Class.forName(testCaseName);
        } catch (ClassNotFoundException e) {
            System.err.println("Test case class not found: " + testCaseName);
            System.exit(1);
            return null;
        }
    }
}

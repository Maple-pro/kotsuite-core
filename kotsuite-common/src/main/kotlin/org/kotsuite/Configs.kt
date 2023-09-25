package org.kotsuite

import org.apache.logging.log4j.Level
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Configs {

    // log related configs
    val sectionLevel: Level = Level.forName("SECTION", 350)
    val successLevel: Level = Level.forName("SUCCESS", 360)
    const val LOG_COMMAND_OUTPUT = false

    // project related configs
    lateinit var projectPath: String
    lateinit var modulePath: String
    lateinit var sourceCodePath: List<String>
    lateinit var classesFilePath: List<String>
    lateinit var mainClass: String
    lateinit var includeRules: List<String>
    lateinit var includeFiles: String
    lateinit var libsPath: String
    lateinit var dependencyClassPaths: List<String>

    // genetic algorithm output paths
    val sootOutputPath: String get() = "$modulePath/sootOutput"
    val kotSuiteOutputPath: String get() = "$modulePath/kotsuite"
    val execOutputPath: String get() = "$kotSuiteOutputPath/exec"
    val jarOutputPath: String get() = "$kotSuiteOutputPath/jar"
    val reportOutputPath: String get() = "$kotSuiteOutputPath/report"

    // final output paths
    val finalOutputPath: String get() = "$modulePath/final"
    val finalClassesOutputPath: String get() = "$finalOutputPath/classes" // have source and test
    val finalTestOutputPath: String get() = "$finalOutputPath/test" // only have test classes
    val finalDecompiledOutputPath: String get() = "$finalOutputPath/decompiled"
    val finalExecOutputPath: String get() = "$finalOutputPath/exec"
    val finalReportOutputPath: String get() = "$finalOutputPath/report"

    // dependency jar paths
    val kotsuiteAgentPath: String get() = "$libsPath/cli/kotsuite-agent-1.2.jar"
    val jacocoAgentPath: String get() = "$libsPath/cli/org.jacoco.agent-0.8.10-runtime.jar"
    val jacocoCliPath: String get() = "$libsPath/cli/org.jacoco.cli-0.8.10-nodeps.jar"
    val kotlinRuntimePath: String get() = "$libsPath/classpath/kotlin-runtime-1.2.71.jar"
    val kotlinStdLibPath: String get() = "$libsPath/classpath/kotlin-stdlib-1.8.10.jar"
//    val decompilerPath: String get() = "$libsPath/cli/java-decompiler.jar"
    val decompilerPath: String get() = "$libsPath/cli/fernflower.jar"

    // genetic algorithm related configs
    const val MAX_ATTEMPT = 50
    const val TARGET_LINE_COVERAGE = 0.6  // Line coverage
    const val TARGET_CC_COVERAGE = 0.9  // Cyclomatic complexity coverage

    /**
     * Get exec file path
     *
     * @param testClassName test class full name, e.g., org.example.myapplication.TempCalleePrintHelloRound0
     * @param testCaseName test case name, e.g, test_printHello_1
     * @return the exec file path, e.g., `$execOutputPath`/jacoco_org.example.myapplication.TempCalleePrintHelloRound0.test_printHello_1
     */
    fun getExecFilePath(testClassName: String, testCaseName: String): String {
        val fileName =
            if (testCaseName == "*") "jacoco_${testClassName}_all.exec"
            else "jacoco_${testClassName}_${testCaseName}.exec"
        return "$execOutputPath/$fileName"
    }

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")

    fun getFinalExecFilePath(dateTime: LocalDateTime): String {
        val timestamp = dateTime.format(timeFormatter)
        return "$finalExecOutputPath/jacoco_$timestamp.exec"
    }

    fun getFinalHTMLReportPath(dateTime: LocalDateTime): String {
        val timestamp = dateTime.format(timeFormatter)
        val htmlReportPath = "$finalReportOutputPath/coverage_report_$timestamp/"
        Files.createDirectories(Paths.get(htmlReportPath))

        return htmlReportPath
    }

    fun getFinalXMLReportPath(dateTime: LocalDateTime): String {
        val timestamp = dateTime.format(timeFormatter)
        return "$finalReportOutputPath/coverage_xml_$timestamp.xml"
    }

    @Override
    override fun toString(): String {
        return "{ project_path: $projectPath, module_path: $modulePath }"
    }

}
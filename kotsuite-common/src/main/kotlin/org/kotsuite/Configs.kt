package org.kotsuite

import org.apache.logging.log4j.Level
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object Configs {
    const val KOTSUITE_CORE_VERSION = "1.2.2"

    private val dateTime = LocalDateTime.now()
    private val timeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    private val timestamp = dateTime.format(timeFormatter)

    const val ONLY_SUCCESS = true // Only output successful test cases

    // log related configs
    val sectionLevel: Level = Level.forName("SECTION", 350)
    val successLevel: Level = Level.forName("SUCCESS", 360)
    const val LOG_COMMAND_OUTPUT = true

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
    val kotsuiteRootOutputPath: String get() = "$modulePath/kotsuite"
    val kotsuiteOutputPath: String get() = "$kotsuiteRootOutputPath/$timestamp"
    val sootOutputPath: String get() = "$kotsuiteOutputPath/sootOutput"
    val finalOutputPath: String get() = "$kotsuiteOutputPath/final"

    // final output paths
    val finalClassesOutputPath: String get() = "$finalOutputPath/classes" // have source and test

    val finalTestOutputPath: String get() = "$finalOutputPath/test" // only have test classes
    val finalSuccessTestOutputPath: String get() = "$finalTestOutputPath/success"
    val finalFailedTestOutputPath: String get() = "$finalTestOutputPath/failed"

    val finalDecompiledOutputPath: String get() = "$finalOutputPath/decompiled"
    val finalSuccessDecompiledOutputPath: String get() = "$finalDecompiledOutputPath/success"
    val finalFailedDecompiledOutputPath: String get() = "$finalDecompiledOutputPath/failed"

    val finalExecOutputPath: String get() = "$finalOutputPath/exec"
    val finalReportOutputPath: String get() = "$finalOutputPath/report"
    
    val execOutputPath: String get() = "$finalOutputPath/exec"
    val assertOutputPath: String get() = "$finalOutputPath/assert"
    val commandOutputPath: String get() = "$finalOutputPath/command"

    // cli jar paths
    val kotsuiteAgentPath: String get() = "$libsPath/cli/kotsuite-agent-shadow-1.2-all.jar"
    val jacocoAgentPath: String get() = "$libsPath/cli/org.jacoco.agent-0.8.10-runtime.jar"
    val jacocoCliPath: String get() = "$libsPath/cli/org.jacoco.cli-0.8.10-nodeps.jar"
    val kotlinRuntimePath: String get() = "$libsPath/classpath/kotlin-runtime-1.2.71.jar"
    val kotlinStdLibPath: String get() = "$libsPath/classpath/kotlin-stdlib-1.8.10.jar"
//    val decompilerPath: String get() = "$libsPath/cli/java-decompiler.jar"
    val decompilerPath: String get() = "$libsPath/cli/fernflower.jar"
    val mainClassFile: String get() = "$libsPath/cli/KotMain.class"

    // genetic algorithm related configs
    const val MAX_ATTEMPT = 1
    const val TARGET_LINE_COVERAGE = 0.6  // Line coverage
    const val TARGET_CC_COVERAGE = 0.9  // Cyclomatic complexity coverage
    const val POPULATION_SIZE = 2

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

    fun getReportFilePath(dateTime: LocalDateTime): String {
        val timestamp = dateTime.format(timeFormatter)
        return "$finalReportOutputPath/report_$timestamp.json"
    }

    fun getStatisticFilePath(dateTime: LocalDateTime): String {
        val timestamp = dateTime.format(timeFormatter)
        return "$finalReportOutputPath/statistic_$timestamp.json"
    }

    fun getOverallStatisticFilePath(): String {
        return "$finalReportOutputPath/overall_statistic.json"
    }

    fun getCommandFilePath(prefix: String, dateTime: LocalDateTime): String {
        val timestamp = dateTime.format(timeFormatter)
        return "$commandOutputPath/${prefix}_arguments_${timestamp}_${Random.nextInt()}.txt"
    }

    fun getLogFilePath(): String {
        return "$kotsuiteOutputPath/kotsuite.log"
    }

    fun getKotSuiteArgumentsFilePath(): String {
        return "$kotsuiteOutputPath/kotsuite-arguments.txt"
    }

    fun getKotSuiteDependencyClassPathFilePath(): String {
        return "$kotsuiteOutputPath/kotsuite-dependency-classpath.txt"
    }

    fun getKotSuiteModuleInformationFilePath(): String {
        return "$kotsuiteOutputPath/kotsuite-module-information.txt"
    }

    @Override
    override fun toString(): String {
        return "{ project_path: $projectPath, module_path: $modulePath }"
    }

}
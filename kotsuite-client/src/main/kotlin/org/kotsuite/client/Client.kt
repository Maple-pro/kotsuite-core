package org.kotsuite.client

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.kotsuite.analysis.Analyzer
import org.kotsuite.ga.Configs
import org.kotsuite.ga.TestSuiteGenerator
import org.kotsuite.ga.strategy.StrategyHelper
import org.kotsuite.ga.coverage.CoverageGenerator
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * This class represents a KotSuite client.
 */
class Client(
    private var projectPath: String,
    private val modulePath: String,
    private val moduleClassPath: String,
    private val moduleSourcePath: String,
    private val includeRules: List<String>,
    private val libsPath: String,
    private val gaStrategy: String,
) {

//    private val log = LoggerFactory.getLogger(this.javaClass)
    private val sectionLevel = Level.forName("SECTION", 350)
    private val successLevel = Level.forName("SUCCESS", 360)
    private val logger = LogManager.getLogger()

    private val testSuiteGenerator: TestSuiteGenerator

    init {
        setConfigs()
        testSuiteGenerator = TestSuiteGenerator(StrategyHelper.getGAStrategy(gaStrategy))
    }

    private fun setConfigs() {
        Configs.projectPath = projectPath
        Configs.modulePath = modulePath
        Configs.sourceCodePath = moduleSourcePath
        Configs.classesFilePath = moduleClassPath
        Configs.sootOutputPath = "${modulePath}/sootOutput/"
        Configs.outputPath = "${modulePath}/kotsuite/"
        Configs.mainClass = "KotMain"
        Configs.includeRules = includeRules
        Configs.includeFiles = "*"
        Configs.libsPath = libsPath

        logger.log(Level.INFO, "Set configs: ${Configs.print()}")
    }

    /**
     * Analysis the given bytecode using soot.
     */
    fun analyze(classPath: String) {
        logger.log(sectionLevel, "[Analysis Phase]")

        Analyzer.projectPath = projectPath
        Analyzer.includeRules = includeRules
        Analyzer.analyze(classPath)
    }

    /**
     * Generate test suite for the give bytecode.
     */
    fun generateTestSuite() {
        logger.log(sectionLevel, "[Generate Phase]")

        Files.createDirectories(Paths.get(Configs.sootOutputPath))
        Files.createDirectories(Paths.get(Configs.outputPath))

        // Copy class files into sootOutput/ directory
        File(Configs.classesFilePath).copyRecursively(File(Configs.sootOutputPath), true)

        testSuiteGenerator.generate()

        logger.log(successLevel, "Success!")
    }

    /**
     * Attach the client to the JVM. Load KotSuite agent on the JVM that runs the code to be tested.
     */
    fun attach() {

    }

    /**
     * Submit generated test case to the JVM attached.
     */
    fun submit() {

    }

    /**
     * Close all connection state to the JVM attached.
     */
    fun close() {

    }

}
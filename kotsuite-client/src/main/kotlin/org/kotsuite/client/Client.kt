package org.kotsuite.client

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.kotsuite.analysis.Analyzer
import org.kotsuite.ga.Configs
import org.kotsuite.ga.TestSuiteGenerator
import org.kotsuite.ga.strategy.StrategyHelper
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

    private val logger = LogManager.getLogger()

    private val testSuiteGenerator: TestSuiteGenerator

    init {
        setConfigs()
        createDirectories()
        testSuiteGenerator = TestSuiteGenerator(StrategyHelper.getGAStrategy(gaStrategy))
    }

    private fun setConfigs() {
        Configs.projectPath = projectPath
        Configs.modulePath = modulePath
        Configs.sourceCodePath = moduleSourcePath
        Configs.classesFilePath = moduleClassPath
        Configs.mainClass = "KotMain"
        Configs.includeRules = includeRules
        Configs.includeFiles = "*"
        Configs.libsPath = libsPath

        logger.log(Level.INFO, "Set configs: $Configs")
    }

    private fun createDirectories() {
        Files.createDirectories(Paths.get(Configs.kotSuiteOutputPath))
        Files.createDirectories(Paths.get(Configs.sootOutputPath))
        Files.createDirectories(Paths.get(Configs.execOutputPath))
        Files.createDirectories(Paths.get(Configs.jarOutputPath))
        Files.createDirectories(Paths.get(Configs.reportOutputPath))

        Files.createDirectories(Paths.get(Configs.finalOutputPath))
        Files.createDirectories(Paths.get(Configs.finalClassesOutputPath))
        Files.createDirectories(Paths.get(Configs.finalDecompiledOutputPath))
        Files.createDirectories(Paths.get(Configs.finalExecOutputPath))
        Files.createDirectories(Paths.get(Configs.finalReportOutputPath))
    }

    /**
     * Analysis the given bytecode using soot.
     */
    fun analyze(classPath: String) {
        logger.log(Configs.sectionLevel, "[Analysis Phase]")

        Analyzer.projectPath = projectPath
        Analyzer.includeRules = includeRules
        Analyzer.analyze(classPath)
    }

    /**
     * Generate test suite for the give bytecode.
     */
    fun generateTestSuite() {
        logger.log(Configs.sectionLevel, "[Generate Phase]")

        // Copy class files into `sootOutput/` and `final/classes/` directory
        File(Configs.classesFilePath).copyRecursively(File(Configs.sootOutputPath), true)
        File(Configs.classesFilePath).copyRecursively(File(Configs.finalClassesOutputPath), true)

        testSuiteGenerator.generate()

        logger.log(Configs.successLevel, "Success!")
    }

}
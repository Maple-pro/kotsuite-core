package org.kotsuite.client

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.kotsuite.analysis.Analyzer
import org.kotsuite.Configs
import org.kotsuite.ga.TestSuiteGenerator
import org.kotsuite.ga.strategy.StrategyHelper
import org.kotsuite.utils.FileUtils
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
    private val dependencyClassPaths: String,
) {

    private val log = LogManager.getLogger()

    private val testSuiteGenerator: TestSuiteGenerator

    init {
        setConfigs()
        clearDirectories()
        createDirectories()
        testSuiteGenerator = TestSuiteGenerator(StrategyHelper.getGAStrategy(gaStrategy))
    }

    private fun setConfigs() {
        Configs.projectPath = projectPath
        Configs.modulePath = modulePath
        Configs.sourceCodePath = moduleSourcePath.split(File.pathSeparator)
        Configs.classesFilePath = moduleClassPath.split(File.pathSeparator)
        Configs.mainClass = "KotMain"
        Configs.includeRules = includeRules
        Configs.includeFiles = "*"
        Configs.libsPath = libsPath
        Configs.dependencyClassPaths = dependencyClassPaths.split(File.pathSeparator).map {
            if (it.endsWith("!/")) {
                it.removeSuffix("!/")
            } else {
                it
            }
        }.filter { it.endsWith(".jar") }

        log.log(Level.INFO, "Set configs: $Configs")
    }

    private fun createDirectories() {
        with(Configs) {
            Files.createDirectories(Paths.get(kotSuiteOutputPath))
            Files.createDirectories(Paths.get(sootOutputPath))
            Files.createDirectories(Paths.get(execOutputPath))
            Files.createDirectories(Paths.get(jarOutputPath))
            Files.createDirectories(Paths.get(reportOutputPath))
            Files.createDirectories(Paths.get(assertOutputPath))

            Files.createDirectories(Paths.get(finalOutputPath))
            Files.createDirectories(Paths.get(finalClassesOutputPath))
            Files.createDirectories(Paths.get(finalTestOutputPath))
            Files.createDirectories(Paths.get(finalDecompiledOutputPath))
            Files.createDirectories(Paths.get(finalExecOutputPath))
            Files.createDirectories(Paths.get(finalReportOutputPath))
        }
    }

    private fun clearDirectories() {
        with(Configs) {
            FileUtils.deleteDirectory(File(kotSuiteOutputPath))
            FileUtils.deleteDirectory(File(sootOutputPath))
            FileUtils.deleteDirectory(File(finalOutputPath))
        }
    }

    /**
     * Analysis the given bytecode using soot.
     */
    fun analyze() {
        log.log(Configs.sectionLevel, "[Analysis Phase]")

        Analyzer.projectPath = projectPath
        Analyzer.includeRules = includeRules
        Analyzer.analyze()
    }

    /**
     * Generate test suite for the give bytecode.
     */
    fun generateTestSuite() {
        log.log(Configs.sectionLevel, "[Generate Phase]")

        // Clear class files in `sootOutput/` and `final/classes/` directory
        File(Configs.sootOutputPath)

        // Copy class files into `sootOutput/` and `final/classes/` directory
        for (path in Configs.classesFilePath) {
            val file = File(path)
            if (file.exists() && file.isDirectory) {
                File(path).copyRecursively(File(Configs.sootOutputPath), true)
                File(path).copyRecursively(File(Configs.finalClassesOutputPath), true)
            }
        }

        testSuiteGenerator.generate()

        log.log(Configs.successLevel, "Success!")
    }

}
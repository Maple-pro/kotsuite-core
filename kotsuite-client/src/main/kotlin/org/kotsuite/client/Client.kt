package org.kotsuite.client

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import org.kotsuite.analysis.Analyzer
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
        val dealtDependencyClassPaths = dependencyClassPaths
            .split(File.pathSeparator)
            .map {
                if (it.endsWith("!/")) {
                    it.removeSuffix("!/")
                } else {
                    it
                }
            }
            .filter {
                it.endsWith(".jar")
            }.toMutableList()

        // Move all android platform jars to the end of the list
        val androidDependencies = dealtDependencyClassPaths.filter { it.contains("android.jar") }
        dealtDependencyClassPaths.removeAll(androidDependencies)
        dealtDependencyClassPaths.addAll(androidDependencies)

        Configs.projectPath = projectPath
        Configs.modulePath = modulePath
        Configs.sourceCodePath = moduleSourcePath.split(File.pathSeparator)
        Configs.classesFilePath = moduleClassPath.split(File.pathSeparator)
        Configs.mainClass = "KotMain"
        Configs.includeRules = includeRules
        Configs.includeFiles = "*"
        Configs.libsPath = libsPath
        Configs.dependencyClassPaths = dealtDependencyClassPaths

        log.log(Level.INFO, "Set configs: $Configs")
    }

    private fun createDirectories() {
        with(Configs) {
            Files.createDirectories(Paths.get(kotsuiteRootOutputPath))
            Files.createDirectories(Paths.get(kotsuiteOutputPath))
            Files.createDirectories(Paths.get(sootOutputPath))
            Files.createDirectories(Paths.get(finalOutputPath))

            Files.createDirectories(Paths.get(execOutputPath))
            Files.createDirectories(Paths.get(assertOutputPath))
            Files.createDirectories(Paths.get(commandOutputPath))

            Files.createDirectories(Paths.get(finalClassesOutputPath))
            Files.createDirectories(Paths.get(finalTestOutputPath))
            Files.createDirectories(Paths.get(finalDecompiledOutputPath))
            Files.createDirectories(Paths.get(finalExecOutputPath))
            Files.createDirectories(Paths.get(finalReportOutputPath))

            File(mainClassFile).copyTo(File("$finalClassesOutputPath/KotMain.class"), true)
            File(mainClassFile).copyTo(File("$sootOutputPath/KotMain.class"), true)
        }
    }

    private fun clearDirectories() {
        with(Configs) {
            FileUtils.deleteDirectory(File(kotsuiteOutputPath))
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
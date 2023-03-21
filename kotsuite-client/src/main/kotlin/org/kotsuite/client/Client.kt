package org.kotsuite.client

import org.kotsuite.analysis.Analyzer
import org.kotsuite.ga.TestSuiteGenerator
import org.kotsuite.ga.StrategyHelper
import org.kotsuite.ga.chromosome.printer.JasminPrinter
import org.kotsuite.ga.chromosome.generator.jimple.JimpleGenerator
import org.kotsuite.ga.coverage.CoverageGenerator
import org.slf4j.LoggerFactory

/**
 * This class represents a KotSuite client.
 */
class Client(private var exampleProjectDir: String,
             private val classesOrPackagesToAnalyze: List<String>,
             private val gaStrategy: String) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Analysis the given bytecode using soot.
     */
    fun analyze() {
        log.info("[Analysis Phase]")

        Analyzer.exampleProjectDir = exampleProjectDir
        Analyzer.classesOrPackagesToAnalyze = classesOrPackagesToAnalyze
        Analyzer.analyze()

    }

    /**
     * Generate test suite for the give bytecode.
     */
    fun generateTestSuite() {
        log.info("[Generate Phase]")

//        val outputFileDir = "$exampleProjectDir/app/build/tmp/kotlin-classes/debug/"
        val outputFileDir = exampleProjectDir

        TestSuiteGenerator.gaStrategy = StrategyHelper.getGAStrategy(gaStrategy)
        val testClasses = TestSuiteGenerator.generate()
        val jimpleClasses = JimpleGenerator.generateClasses(testClasses)

        jimpleClasses.forEach {
            JasminPrinter(outputFileDir).printJasminFile(it)
        }

        val coverageGenerator = CoverageGenerator(
            "$exampleProjectDir/kotsuite/MyApplication.jar",
            "$exampleProjectDir/app/build/tmp/kotlin-classes/debug/",
            "$exampleProjectDir/sootOutput/report/jacoco-MyApplication.exec",
        )
        coverageGenerator.generateExecFile("ExampleTest", "*")
        coverageGenerator.getCoverageInfo()
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
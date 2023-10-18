package org.kotsuite.ga

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.printer.JasminPrinter
import org.kotsuite.ga.coverage.ExecResolver
import org.kotsuite.ga.coverage.JacocoUtils
import org.kotsuite.ga.decompile.Decompiler
import org.kotsuite.ga.solution.WholeSolution
import org.kotsuite.ga.strategy.Strategy
import soot.SootClass
import java.time.LocalDateTime
import org.kotsuite.Configs
import org.kotsuite.ga.jimple.JimpleGenerator.generateJimpleTestClasses

class TestSuiteGenerator(private val gaStrategy: Strategy) {

    private val log = LogManager.getLogger()

    private lateinit var wholeSolution: WholeSolution
    private lateinit var testClasses: List<TestClass>
    private lateinit var jimpleClasses: List<SootClass>
    private lateinit var jimpleClassesWithAssertion: List<SootClass>

    /**
     * Generate test cases using given strategy
     */
    fun generate(): WholeSolution {
        log.log(Configs.sectionLevel, "[Test suite generator: $gaStrategy]")

        // generate whole solution using the given strategy
        wholeSolution = gaStrategy.generateWholeSolution()

        jimpleClasses = wholeSolution.generateJimpleTestClasses(false)
        jimpleClassesWithAssertion = wholeSolution.generateJimpleTestClasses(true)

        // generate whole solution coverage report
        generateFinalCoverageReport()

        // decompile class file to java file
        printTestClassJasminFiles(Configs.finalTestOutputPath)
        decompileJUnitClasses()

        return wholeSolution
    }

    private fun generateFinalCoverageReport() {
        log.log(Configs.sectionLevel, "[Generate final whole solution coverage report]")

        if (wholeSolution.classSolutions.isEmpty()) {
            log.log(Level.WARN, "No generated class solution")
            return
        }

        testClasses = wholeSolution.classSolutions.map { it.testClass }

        printJasminFiles(Configs.finalClassesOutputPath)

        val dateTime = LocalDateTime.now()  // use dateTime to identify the same exec file and report
        val execDataFile = Configs.getFinalExecFilePath(dateTime)
        JacocoUtils.generateFinalWholeSolutionExecFile(
            wholeSolution,
            Configs.mainClass,
            execDataFile,
            Configs.finalClassesOutputPath,
        )

        val execResolver = ExecResolver(
            "MyApplication",
            execDataFile,
            Configs.finalClassesOutputPath,
            Configs.sourceCodePath,
        )
        val finalHTMLReportPath = Configs.getFinalHTMLReportPath(dateTime)
        val finalXMLReportPath = Configs.getFinalXMLReportPath(dateTime)
        execResolver.generateHTMLReport(finalHTMLReportPath)
        execResolver.generateXMLReport(finalXMLReportPath)
    }

    private fun decompileJUnitClasses() {
        log.log(Configs.sectionLevel, "[Decompile class files to java files]")

        Decompiler.decompileJasminToJava(Configs.finalTestOutputPath, Configs.finalDecompiledOutputPath)
    }

    /**
     * Print generated test classes and dummy main class to bytecode
     * For coverage generation, don't have assertions
     */
    private fun printJasminFiles(outputPath: String) {
        jimpleClasses.forEach {
            JasminPrinter.printJasminFile(it, outputPath)
        }
    }

    /**
     * Print test class jasmin files
     * For final decompile, have assertions
     *
     * @param outputPath
     */
    private fun printTestClassJasminFiles(outputPath: String) {
        jimpleClassesWithAssertion.forEach {
            JasminPrinter.printJasminFile(it, outputPath)
        }
    }
}
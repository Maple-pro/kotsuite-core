package org.kotsuite.ga

import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.chromosome.jimple.JimpleGenerator
import org.kotsuite.ga.chromosome.printer.JasminPrinter
import org.kotsuite.ga.coverage.ExecResolver
import org.kotsuite.ga.coverage.JacocoUtils
import org.kotsuite.ga.solution.WholeSolution
import org.kotsuite.ga.strategy.Strategy
import org.kotsuite.ga.utils.SootUtils
import org.slf4j.LoggerFactory
import soot.SootClass
import java.time.LocalDateTime

class TestSuiteGenerator(private val gaStrategy: Strategy) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private lateinit var wholeSolution: WholeSolution
    private lateinit var testClasses: List<TestClass>
    private lateinit var jimpleClasses: List<SootClass>
    private lateinit var dummyMainClass: SootClass

    /**
     * Generate test cases using given strategy
     */
    fun generate(): WholeSolution {
        log.info("Generator Strategy: $gaStrategy")

        wholeSolution = gaStrategy.generateWholeSolution()

        jimpleClasses = JimpleGenerator.generateTestClassesFromWholeSolution(wholeSolution)

        printAndReport()

        return wholeSolution
    }

    /**
     * Print and report for standard ga. Successive operation for standard ga strategy
     *
     */
    private fun printAndReport() {
        // 1. generate whole solution coverage report
        generateFinalCoverageReport()

        // 2. decompile class file to java file
        decompileJUnitClasses()
    }

    private fun generateFinalCoverageReport() {
        testClasses = wholeSolution.classSolutions.map { it.testClass }

        dummyMainClass = generateDummyMainClass()

        printJasminFiles(Configs.finalClassesOutputPath)

        val dateTime = LocalDateTime.now()  // use dateTime to identify the same exec file and report
        val execDataFile = Configs.getFinalExecFilePath(dateTime)
        JacocoUtils.generateExecFileWithKotsuiteAgent(
            Configs.finalClassesOutputPath,
            execDataFile,
            "",
            "",
            dummyMainClass.name,
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

//        CoverageGenerator.generate()
    }

    private fun decompileJUnitClasses() {
//        Configs.finalDecompiledOutputPath
//        TODO()
    }

    private fun generateDummyMainClass(): SootClass {
        // Generate main class and main method to call all test cases
        val targetMethods = jimpleClasses
            .map { it.methods }
            .reduce { methods, method -> methods + method }
            .filter { SootUtils.filterConstructorMethod(it) }

        return SootUtils.generateMainClass(Configs.mainClass, targetMethods)
    }

    /**
     * Print generated test cases to bytecode
     */
    private fun printJasminFiles(outputPath: String) {
        jimpleClasses.forEach {
            JasminPrinter.printJasminFile(it, outputPath)
        }
        JasminPrinter.printJasminFile(dummyMainClass, outputPath)
    }

}
package org.kotsuite.ga

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.chromosome.jimple.JimpleGenerator
import org.kotsuite.ga.chromosome.printer.JasminPrinter
import org.kotsuite.ga.coverage.ExecResolver
import org.kotsuite.ga.coverage.JacocoUtils
import org.kotsuite.ga.decompile.Decompiler
import org.kotsuite.ga.solution.WholeSolution
import org.kotsuite.ga.strategy.Strategy
import org.kotsuite.ga.utils.SootUtils
import soot.SootClass
import java.time.LocalDateTime

class TestSuiteGenerator(private val gaStrategy: Strategy) {

    private val logger = LogManager.getLogger()

    private lateinit var wholeSolution: WholeSolution
    private lateinit var testClasses: List<TestClass>
    private lateinit var jimpleClasses: List<SootClass>
    private lateinit var jimpleClassesWithAssertion: List<SootClass>
    private lateinit var dummyMainClass: SootClass

    /**
     * Generate test cases using given strategy
     */
    fun generate(): WholeSolution {
        logger.log(Configs.sectionLevel, "[Test suite generator: $gaStrategy]")

        // generate whole solution using the given strategy
        wholeSolution = gaStrategy.generateWholeSolution()

        jimpleClasses = JimpleGenerator.generateTestClassesFromWholeSolution(wholeSolution, false)
        jimpleClassesWithAssertion = JimpleGenerator.generateTestClassesFromWholeSolution(wholeSolution, true)

        // generate whole solution coverage report
        generateFinalCoverageReport()

        // decompile class file to java file
        printTestClassJasminFiles(Configs.finalTestOutputPath)
        decompileJUnitClasses()

        return wholeSolution
    }

    private fun generateFinalCoverageReport() {
        logger.log(Configs.sectionLevel, "Generate final whole solution coverage report")

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
        logger.log(Configs.sectionLevel, "Decompile class files to java files")

        Decompiler.decompileJasminToJava(Configs.finalTestOutputPath, Configs.finalDecompiledOutputPath)
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
     * Print generated test classes and dummy main class to bytecode
     * For coverage generation, don't have assertions
     */
    private fun printJasminFiles(outputPath: String) {
        jimpleClasses.forEach {
            JasminPrinter.printJasminFile(it, outputPath)
        }
        JasminPrinter.printJasminFile(dummyMainClass, outputPath)
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
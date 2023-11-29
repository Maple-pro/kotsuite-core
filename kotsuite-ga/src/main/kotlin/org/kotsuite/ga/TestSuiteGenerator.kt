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
import org.kotsuite.analysis.Analyzer
import org.kotsuite.ga.jimple.JimpleGenerator.generateJimpleTestClasses
import org.kotsuite.ga.report.Report
import org.kotsuite.ga.statistic.Statistic
import java.io.File

class TestSuiteGenerator(private val gaStrategy: Strategy) {

    private val log = LogManager.getLogger()

    private val dateTime = LocalDateTime.now()  // use dateTime to identify the same exec file and report

    private lateinit var wholeSolution: WholeSolution
    private lateinit var successWholeSolution: WholeSolution
    private lateinit var failedWholeSolution: WholeSolution
    private lateinit var testClasses: List<TestClass>
    private lateinit var jimpleClasses: List<SootClass>
    private lateinit var jimpleClassesWithAssertion: List<SootClass>
    private lateinit var successJimpleClassesWithAssertion: List<SootClass>
    private lateinit var failedJimpleClassesWithAssertion: List<SootClass>

    /**
     * Generate test cases using given strategy
     */
    fun generate(): WholeSolution {
        log.log(Configs.sectionLevel, "[Test suite generator: $gaStrategy]")

        // generate whole solution using the given strategy
        wholeSolution = gaStrategy.generateWholeSolution()

        // except crashed test cases
        wholeSolution = wholeSolution.exceptCrashedWholeSolution()

        successWholeSolution = wholeSolution.getSuccessfulWholeSolution()
        failedWholeSolution = wholeSolution.getFailedWholeSolution()

        jimpleClasses = wholeSolution.generateJimpleTestClasses(false)
        jimpleClassesWithAssertion = wholeSolution.generateJimpleTestClasses(true)
        successJimpleClassesWithAssertion = successWholeSolution.generateJimpleTestClasses(true)
        failedJimpleClassesWithAssertion = failedWholeSolution.generateJimpleTestClasses(true)

        // generate whole solution coverage report
        generateFinalCoverageReport()

        // Generate the generation report (json)
        val report = Report.fromWholeSolution(wholeSolution, Analyzer.classes)
        val statistic = Statistic.fromReport(report)

        // save `Report` to file
        val finalReportPath = Configs.getReportFilePath(dateTime)
        saveReportToFile(report, finalReportPath)

        // save `Statistic` to file
        val finalStatisticPath = Configs.getStatisticFilePath(dateTime)
        saveStatisticToFile(statistic, finalStatisticPath)

        // decompile class file to java file
//        printTestClassJasminFiles(Configs.finalTestOutputPath)
        printJasminFiles(successJimpleClassesWithAssertion, Configs.finalSuccessTestOutputPath)
        printJasminFiles(failedJimpleClassesWithAssertion, Configs.finalFailedTestOutputPath)
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

        log.debug("a. Output .class files to ${Configs.finalClassesOutputPath}")
//        printJasminFiles(Configs.finalClassesOutputPath)
        printJasminFiles(jimpleClasses, Configs.finalClassesOutputPath)

        log.debug("b. Generate final exec file")
        val execDataFile = Configs.getFinalExecFilePath(dateTime)
        JacocoUtils.generateFinalWholeSolutionExecFile(
            wholeSolution,
            Configs.mainClass,
            execDataFile,
            Configs.finalClassesOutputPath,
        )

        log.debug("c. Generate coverage, report and statistic")
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

        // Update coverage info to `WholeSolution`: class and method
        wholeSolution.updateCoverageInfo(execResolver)
    }

    private fun decompileJUnitClasses() {
        log.log(Configs.sectionLevel, "[Decompile class files to java files]")

        Decompiler.decompileJasminToJava(Configs.finalSuccessTestOutputPath, Configs.finalSuccessDecompiledOutputPath)
        Decompiler.decompileJasminToJava(Configs.finalFailedTestOutputPath, Configs.finalFailedDecompiledOutputPath)
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

    private fun printJasminFiles(sootClasses: List<SootClass>, outputPath: String) {
        sootClasses.forEach {
            JasminPrinter.printJasminFile(it, outputPath)
        }
    }

    private fun saveReportToFile(report: Report, filePath: String) {
        File(filePath).writeText(report.toString())
    }

    private fun saveStatisticToFile(statistic: Statistic, filePath: String) {
        File(filePath).writeText(statistic.toString())
    }
}
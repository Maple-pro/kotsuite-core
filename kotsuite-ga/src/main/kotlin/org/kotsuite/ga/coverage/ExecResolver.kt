package org.kotsuite.ga.coverage

import org.apache.logging.log4j.LogManager
import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.analysis.IBundleCoverage
import org.jacoco.core.analysis.ICounter
import org.jacoco.core.analysis.IMethodCoverage
import org.jacoco.core.internal.analysis.ClassCoverageImpl
import org.jacoco.core.internal.analysis.CounterImpl
import org.jacoco.core.internal.analysis.LineImpl
import org.jacoco.core.tools.ExecFileLoader
import org.kotsuite.Configs
import org.kotsuite.ga.coverage.fitness.Fitness
import org.kotsuite.soot.SootUtils
import org.kotsuite.utils.getError
import org.kotsuite.utils.getOutput
import org.kotsuite.utils.logCommandOutput
import soot.SootClass
import soot.SootMethod
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class ExecResolver(
    private val title: String,
    private val executionDataFilePath: String,
    private val classesFilePath: String,
    private val sourcesFilePath: List<String>?,
) {
    private val log = LogManager.getLogger()

    private val executionDataFile = File(executionDataFilePath)
    private val classesFile = File(classesFilePath)
    private val execFileLoader = ExecFileLoader()
    private val coverageBuilder = CoverageBuilder()
    private lateinit var bundleCoverage: IBundleCoverage

    init {
        loadExecutionData()
        analyzeStructure()
    }

    /**
     * Get simple coverage information
     */
    fun getSimpleInfo() {
        coverageBuilder.classes.forEach {
            log.info("Coverage of class: ${it.name}-----------------")

            printCounter("instructions", it.instructionCounter)
            printCounter("branches", it.branchCounter)
            printCounter("lines", it.lineCounter)
        }
    }

    /**
     * Get target class fitness
     *
     * @param targetClass
     * @return fitness of target class
     */
    fun getTargetClassFitness(targetClass: SootClass): Fitness {
        return getFitnessByClassName(targetClass.name)
    }

    /**
     * Get target method fitness
     *
     * @param targetMethod
     * @return fitness of target method
     */
    fun getTargetMethodFitness(targetMethod: SootMethod): Fitness {
        return getFitnessByMethodSig(targetMethod.signature)
    }

    /**
     * Get class fitness by class name
     *
     * @param className target class name
     * @return fitness of target class
     */
    fun getFitnessByClassName(className: String): Fitness {
        val targetClassCoverage = coverageBuilder.classes.firstOrNull {
            it.name == className.replace('.', '/')
        }

        if (targetClassCoverage == null) {
            log.error("Target class coverage not found: $className")
            return Fitness(0.0, 0.0)
        }

        val targetClassLineCounter = targetClassCoverage.lineCounter
        val targetClassCCCounter = targetClassCoverage.complexityCounter

        return Fitness(
            getCoveredRatio(targetClassLineCounter),
            getCoveredRatio(targetClassCCCounter),
        )
    }

    /**
     * Get method fitness by method signature
     *
     * @param methodSig target method signature
     * @return fitness of target method
     */
    fun getFitnessByMethodSig(methodSig: String): Fitness {
        val classNameAndMethodName = SootUtils.getClassNameAndMethodNameFromMethodSig(methodSig)
        val className = classNameAndMethodName.first
        val methodName = classNameAndMethodName.second

        val targetMethodCoverage = coverageBuilder.classes
            .filter {
                it.name.startsWith(className.replace('.', '/'))
            }
            .fold(listOf<IMethodCoverage>()) { sum, item ->
                sum + item.methods
            }
            .firstOrNull {
                it.name == methodName
            }

        if (targetMethodCoverage == null) {
            log.error("Target method coverage not found: $methodSig")
            return Fitness(0.0, 0.0)
        }

        val targetMethodLineCounter = targetMethodCoverage.lineCounter
        val targetMethodCCCounter = targetMethodCoverage.complexityCounter

        return Fitness(
            getCoveredRatio(targetMethodLineCounter),
            getCoveredRatio(targetMethodCCCounter),
        )
    }

    /**
     * Get coverage hash code of target class by class name
     *
     * @param className target class name
     * @return coverage hash code of target class
     */
    fun getCoverageHashCodeByClassName(className: String): List<Int> {
        val targetClassCoverage = coverageBuilder.classes.firstOrNull {
            it.name == className.replace('.', '/')
        }

        return if (targetClassCoverage == null) {
            emptyList()
        } else {
            (targetClassCoverage as ClassCoverageImpl).getHashCodes()
        }
    }

    /**
     * Generate HTML coverage report
     *
     * @param coverageHTMLReportPath path to store HTML report
     */
    fun generateHTMLReport(coverageHTMLReportPath: String) {
        log.info("Generating HTML report: $coverageHTMLReportPath")

        Files.createDirectories(Paths.get(coverageHTMLReportPath))

        val args =
            if (sourcesFilePath != null) {
                arrayOf("java", "-jar",
                    Configs.jacocoCliPath,
                    "report", executionDataFilePath,
                    "--classfile=$classesFilePath",
                    "--sourcefile=${sourcesFilePath.joinToString(File.pathSeparator)}",
                    "--html", coverageHTMLReportPath,
                )
            } else {
                arrayOf("java", "-jar",
                    Configs.jacocoCliPath,
                    "report", executionDataFilePath,
                    "--classfile=$classesFilePath",
                    "--html", coverageHTMLReportPath,
                )
            }

        val ps = Runtime.getRuntime().exec(args)
        val psOutput = ps.getOutput()
        val psError = ps.getError()
        log.logCommandOutput(psOutput, psError)
        ps.waitFor()
    }

    /**
     * Generate XML report
     *
     * @param coverageXMLReportPath path to store XML report
     */
    fun generateXMLReport(coverageXMLReportPath: String) {
        log.info("Generating XML report: $coverageXMLReportPath")

        val args =
            if (sourcesFilePath != null) {
                arrayOf("java", "-jar",
                    Configs.jacocoCliPath,
                    "report", executionDataFilePath,
                    "--classfile=$classesFilePath",
                    "--sourcefile=${sourcesFilePath.joinToString(File.pathSeparator)}",
                    "--xml", coverageXMLReportPath,
                )
            } else {
                arrayOf("java", "-jar",
                    Configs.jacocoCliPath,
                    "report", executionDataFilePath,
                    "--classfile=$classesFilePath",
                    "--xml", coverageXMLReportPath,
                )
            }

        val ps = Runtime.getRuntime().exec(args)
        val psOutput = ps.getOutput()
        val psError = ps.getError()
        log.logCommandOutput(psOutput, psError)
        ps.waitFor()
    }

    /**
     * Log information of given counter
     *
     * @param unit counter description, e.g., instruction, branch, line
     * @param counter counter to be logged
     * @return coverage percent of given counter
     */
    private fun printCounter(unit: String, counter: ICounter): Double {
        val coveragePercent = if (counter.totalCount != 0) {
            counter.coveredCount.toDouble()/counter.totalCount.toDouble()
        } else {
            0.0
        }

        log.info("$unit: ${counter.coveredCount}/${counter.totalCount}, ${String.format("%.2f, $coveragePercent")}")

        return coveragePercent
    }

    /**
     * Calculates the ratio of covered to total count items. If the total count is zero, the ratio is zero.
     *
     * @param counter counter to be calculated
     * @return ratio of covered to total count items
     */
    private fun getCoveredRatio(counter: ICounter): Double {
        return if (counter.totalCount != 0) {
            counter.coveredCount.toDouble()/counter.totalCount.toDouble()
        } else {
            0.0
        }
    }

    /**
     * Get hash code of the [ClassCoverageImpl]
     */
    private fun ClassCoverageImpl.getHashCodes(): List<Int> {
        val lines = this.getLines()
        return lines.map { it.getHashCode() }
    }

    private fun LineImpl.getHashCode(): Int {
        return (this.instructionCounter as CounterImpl).getHashCode() + 31 * (this.branchCounter as CounterImpl).getHashCode()
    }

    private fun CounterImpl.getHashCode(): Int {
        return this.coveredCount + 31 * this.missedCount
    }

    private fun ClassCoverageImpl.getLines(): List<LineImpl> {
        val lines = mutableListOf<LineImpl>()
        for (i in this.firstLine..this.lastLine) {
            val line = this.getLine(i)
            if (line != LineImpl.EMPTY) {
                lines.add(line)
            }
        }

        return lines
    }

    private fun loadExecutionData() {
        execFileLoader.load(executionDataFile)
    }

    private fun analyzeStructure(): IBundleCoverage {
        val analyzer = Analyzer(execFileLoader.executionDataStore, coverageBuilder)
        analyzer.analyzeAll(classesFile)
        return coverageBuilder.getBundle(title)
    }
}
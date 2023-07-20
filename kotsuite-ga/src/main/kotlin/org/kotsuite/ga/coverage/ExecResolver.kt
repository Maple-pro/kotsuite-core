package org.kotsuite.ga.coverage

import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.analysis.IBundleCoverage
import org.jacoco.core.analysis.ICounter
import org.jacoco.core.tools.ExecFileLoader
import org.kotsuite.ga.Configs
import org.kotsuite.ga.coverage.fitness.Fitness
import org.kotsuite.ga.utils.LoggerUtils
import org.slf4j.LoggerFactory
import soot.SootMethod
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class ExecResolver(
    private val title: String,
    private val executionDataFilePath: String,
    private val classesFilePath: String,
    private val sourcesFilePath: String?,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val executionDataFile = File(executionDataFilePath)
    private val classesFile = File(classesFilePath)
    private val execFileLoader = ExecFileLoader()
    private val coverageBuilder = CoverageBuilder()
    private lateinit var bundleCoverage: IBundleCoverage

    init {
        loadExecutionData()
        analyzeStructure()
    }

    fun getSimpleInfo() {
        coverageBuilder.classes.forEach {
            log.info("Coverage of class: ${it.name}-----------------")

            printCounter("instructions", it.instructionCounter)
            printCounter("branches", it.branchCounter)
            printCounter("lines", it.lineCounter)
        }
    }

    fun getTargetMethodFitness(targetMethod: SootMethod): Fitness {
        val targetClass = targetMethod.declaringClass

        val targetMethodCoverage = coverageBuilder.classes.firstOrNull {
            it.name == targetClass.name.replace('.', '/')
        }?.methods?.firstOrNull {
            it.name == targetMethod.name
        }

        if (targetMethodCoverage == null) {
            log.error("Target method coverage not found: ${targetMethod.name}")
            return Fitness(0.0, 0.0)
        }

        val targetMethodLineCounter = targetMethodCoverage.lineCounter
        val targetMethodCCCounter = targetMethodCoverage.complexityCounter

        return Fitness(
            counter2Percent(targetMethodLineCounter),
            counter2Percent(targetMethodCCCounter),
        )
    }

    fun generateHTMLReport(coverageHTMLReportPath: String) {
        log.info("Generating HTML report: $coverageHTMLReportPath")

        Files.createDirectories(Paths.get(coverageHTMLReportPath))

        val args =
            if (sourcesFilePath != null) {
                arrayOf("java", "-jar",
                    Configs.jacocoCliPath,
                    "report", executionDataFilePath,
                    "--classfile=$classesFilePath",
                    "--sourcefile=$sourcesFilePath",
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
        LoggerUtils.logCommandOutput(log, ps, Configs.showDebugLog)
        ps.waitFor()
    }

    fun generateXMLReport(coverageXMLReportPath: String) {
        log.info("Generating XML report: $coverageXMLReportPath")

        val args =
            if (sourcesFilePath != null) {
                arrayOf("java", "-jar",
                    Configs.jacocoCliPath,
                    "report", executionDataFilePath,
                    "--classfile=$classesFilePath",
                    "--sourcefile=$sourcesFilePath",
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
        LoggerUtils.logCommandOutput(log, ps, Configs.showDebugLog)
        ps.waitFor()
    }

    private fun printCounter(unit: String, counter: ICounter): Double {
        val coveragePercent = if (counter.totalCount != 0) {
            counter.coveredCount.toDouble()/counter.totalCount.toDouble()
        } else {
            0.0
        }

        log.info("$unit: ${counter.coveredCount}/${counter.totalCount}, ${String.format("%.2f, $coveragePercent")}")

        return coveragePercent
    }

    private fun counter2Percent(counter: ICounter): Double {
        return if (counter.totalCount != 0) {
            counter.coveredCount.toDouble()/counter.totalCount.toDouble()
        } else {
            0.0
        }
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
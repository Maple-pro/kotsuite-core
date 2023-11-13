package org.kotsuite.ga.coverage

import org.apache.logging.log4j.LogManager
import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.analysis.IBundleCoverage
import org.jacoco.core.analysis.ICounter
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

    fun getSimpleInfo() {
        coverageBuilder.classes.forEach {
            log.info("Coverage of class: ${it.name}-----------------")

            printCounter("instructions", it.instructionCounter)
            printCounter("branches", it.branchCounter)
            printCounter("lines", it.lineCounter)
        }
    }

    fun getTargetClassFitness(targetClass: SootClass): Fitness {
        return getFitnessByClassName(targetClass.name)
    }

    fun getTargetMethodFitness(targetMethod: SootMethod): Fitness {
        return getFitnessByMethodSig(targetMethod.signature)
    }

    fun getFitnessByClassName(className: String): Fitness {
        val targetClassCoverage = coverageBuilder.classes.firstOrNull {
            it.name == className.replace('.', File.separatorChar)
        }

        if (targetClassCoverage == null) {
            log.error("Target class coverage not found: $className")
            return Fitness(0.0, 0.0)
        }

        val targetClassLineCounter = targetClassCoverage.lineCounter
        val targetClassCCCounter = targetClassCoverage.complexityCounter

        return Fitness(
            counter2Percent(targetClassLineCounter),
            counter2Percent(targetClassCCCounter),
        )
    }

    fun getFitnessByMethodSig(methodSig: String): Fitness {
        val classNameAndMethodName = SootUtils.getClassNameAndMethodNameFromMethodSig(methodSig)
        val className = classNameAndMethodName.first
        val methodName = classNameAndMethodName.second

        val targetMethodCoverage = coverageBuilder.classes.firstOrNull {
            it.name == className.replace('.', File.separatorChar)
        }?.methods?.firstOrNull {
            it.name == methodName
        }

        if (targetMethodCoverage == null) {
            log.error("Target method coverage not found: $methodSig")
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
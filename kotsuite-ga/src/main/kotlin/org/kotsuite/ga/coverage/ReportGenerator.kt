package org.kotsuite.ga.coverage

import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.analysis.IBundleCoverage
import org.jacoco.core.analysis.ICounter
import org.jacoco.core.tools.ExecFileLoader
import org.slf4j.LoggerFactory
import java.io.File

class ReportGenerator(
    private val title: String,
    executionDataFilePath: String,
    classesFilePath: String
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val executionDataFile = File(executionDataFilePath)
    private val classesFile = File(classesFilePath)
    private val execFileLoader = ExecFileLoader()
    private val coverageBuilder = CoverageBuilder()
    private var bundleCoverage: IBundleCoverage? = null

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

    fun getMethodCoverage() {


    }

    private fun printCounter(unit: String, counter: ICounter) {
        val coveragePercent = if (counter.totalCount != 0) {
            String.format("%.2f", counter.coveredCount.toDouble()/counter.totalCount.toDouble())
        } else {
            0
        }
        log.info("$unit: ${counter.coveredCount}/${counter.totalCount}, $coveragePercent")
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
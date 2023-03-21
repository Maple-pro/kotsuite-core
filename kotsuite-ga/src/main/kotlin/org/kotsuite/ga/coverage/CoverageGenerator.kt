package org.kotsuite.ga.coverage

import org.kotsuite.ga.utils.LoggerUtils
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CoverageGenerator(
    private val sourceCodePath: String,
    private val classesFilePath: String,
    private val sootOutputPath: String,
    outputPath: String,
    private val mainClass: String,
    private val includeFiles: String,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val jacocoAgentPath = "../lib/jacocoagent.jar"
    private val jacocoCliPath = "../lib/jacococli.jar"
    private val kotlinRunTimePath = "../lib/kotlin-runtime-1.2.71.jar"
    private val kotlinStdLibPath = "../lib/kotlin-stdlib-1.8.10.jar"

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    private val timestamp = LocalDateTime.now().format(timeFormatter)

    private val jarPath = "$outputPath/jar/MyApplication.jar"
    private val executionDataPath = "$outputPath/report/jacoco-MyApplication.exec"
    private val coverageReportPath = "$outputPath/report/coverage_report_$timestamp/"

    fun generate() {
        generateJarFile()
        generateExecFile()
        getCoverageInfo()
        generateHTMLReport()
    }

    private fun generateJarFile() {
        log.info("Package class files into .jar file: $jarPath")

        // Run command `jar -cvf jarPath -C sootOutputPath .`
        val args = arrayOf("jar", "-cvf", jarPath, "-C", sootOutputPath, ".")
        val ps = Runtime.getRuntime().exec(args)
        ps.waitFor()
    }

    private fun generateExecFile() {
        log.info("Main class: $mainClass")
        log.info("Execution data path: $executionDataPath")

        val vmOption = "-javaagent:$jacocoAgentPath=includes=$includeFiles,excludes=CalleeTest,destfile=$executionDataPath,output=file"
        val runtimeJars = "$jarPath;$kotlinRunTimePath;$kotlinStdLibPath"
        val args = arrayOf("java", vmOption, "-cp", runtimeJars, mainClass)
        try {
            val ps = Runtime.getRuntime().exec(args)
            LoggerUtils.logCommandOutput(log, ps)
            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }

    private fun getCoverageInfo() {
        ReportGenerator("MyApplication", executionDataPath, classesFilePath).getSimpleInfo()
    }

    private fun generateHTMLReport() {
        log.info("Generating HTML report: $coverageReportPath")

        Files.createDirectory(Paths.get(coverageReportPath))

        val args = arrayOf("java", "-jar",
            jacocoCliPath,
            "report", executionDataPath,
            "--classfile=$sootOutputPath",
            "--sourcefile=$sourceCodePath",
            "--html", coverageReportPath
        )

        val ps = Runtime.getRuntime().exec(args)
        LoggerUtils.logCommandOutput(log, ps)
        ps.waitFor()
    }

}
package org.kotsuite.ga.coverage

import org.kotsuite.ga.Configs
import org.kotsuite.ga.utils.LoggerUtils
import org.kotsuite.ga.utils.Utils.isLinux
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CoverageGenerator {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val sourceCodePath = Configs.sourceCodePath
    private val sootOutputPath = Configs.sootOutputPath
    private val mainClass = Configs.mainClass
    private val includeFiles = Configs.includeFiles

    private val jacocoAgentPath = Configs.jacocoAgentPath
    private val kotlinRunTimePath = Configs.kotlinRuntimePath
    private val kotlinStdLibPath = Configs.kotlinStdLibPath

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    private val timestamp = LocalDateTime.now().format(timeFormatter)

    private val jarPath = "${Configs.jarOutputPath}/MyApplication.jar"
    private val executionDataPath = "${Configs.execOutputPath}/jacoco-MyApplication.exec"
    private val coverageHTMLReportPath = "${Configs.reportOutputPath}/coverage_report_$timestamp/"
    private val coverageXMLFilePath = "${Configs.reportOutputPath}/coverage_xml_$timestamp.xml"

    @Deprecated("Should not use")
    fun generate() {
        generateJarFile()
        generateExecFile()
        getCoverageInfo()

        val execResolver = ExecResolver(
            "MyApplication",
            executionDataPath,
            sootOutputPath,
            sourceCodePath,
        )
        execResolver.generateHTMLReport(coverageHTMLReportPath)
        execResolver.generateXMLReport(coverageXMLFilePath)
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

        val runtimeJars =
            if (isLinux()) "$jarPath:$kotlinRunTimePath:$kotlinStdLibPath"
            else "$jarPath:$kotlinRunTimePath:$kotlinStdLibPath"

        val args = arrayOf("java", vmOption, "-cp", runtimeJars, mainClass)
        try {
            log.info("Run command: ${args.joinToString(" ")}")
            val ps = Runtime.getRuntime().exec(args)
            LoggerUtils.logCommandOutput(log, ps)
            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }

    private fun getCoverageInfo() {
        ExecResolver("MyApplication", executionDataPath, sootOutputPath, sourceCodePath).getSimpleInfo()
    }

}
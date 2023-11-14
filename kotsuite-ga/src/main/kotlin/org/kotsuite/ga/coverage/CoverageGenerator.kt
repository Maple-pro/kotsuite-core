package org.kotsuite.ga.coverage

import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import org.kotsuite.utils.FileUtils.isLinux
import org.kotsuite.utils.getError
import org.kotsuite.utils.getOutput
import org.kotsuite.utils.logCommandOutput
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Deprecated("No longer used")
object CoverageGenerator {

    private val log = LogManager.getLogger()

    private val sourceCodePath = Configs.sourceCodePath
    private val sootOutputPath = Configs.sootOutputPath
    private val mainClass = Configs.mainClass
    private val includeFiles = Configs.includeFiles

    private val jacocoAgentPath = Configs.jacocoAgentPath
    private val kotlinRunTimePath = Configs.kotlinRuntimePath
    private val kotlinStdLibPath = Configs.kotlinStdLibPath

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    private val timestamp = LocalDateTime.now().format(timeFormatter)

//    private val jarPath = "${Configs.jarOutputPath}/MyApplication.jar"
    private val jarPath = ""
    private val executionDataPath = "${Configs.execOutputPath}/jacoco-MyApplication.exec"
//    private val coverageHTMLReportPath = "${Configs.reportOutputPath}/coverage_report_$timestamp/"
    private val coverageHTMLReportPath = ""
//    private val coverageXMLFilePath = "${Configs.reportOutputPath}/coverage_xml_$timestamp.xml"
    private val coverageXMLFilePath = ""

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
            val psOut = ps.getOutput()
            val psError = ps.getError()
            log.logCommandOutput(psOut, psError)
            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }

    private fun getCoverageInfo() {
        ExecResolver("MyApplication", executionDataPath, sootOutputPath, sourceCodePath).getSimpleInfo()
    }

}
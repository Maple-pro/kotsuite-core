package org.kotsuite.ga.coverage

import org.kotsuite.ga.Configs
import org.kotsuite.ga.chromosome.generator.jimple.Utils
import org.kotsuite.ga.chromosome.printer.JasminPrinter
import org.kotsuite.ga.utils.LoggerUtils
import org.slf4j.LoggerFactory
import soot.SootMethod
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CoverageGenerator {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val sourceCodePath = Configs.sourceCodePath
    private val classesFilePath = Configs.classesFilePath
    private val sootOutputPath = Configs.sootOutputPath
    private val outputPath = Configs.outputPath
    private val mainClass = Configs.mainClass
    private val includeFiles = Configs.includeFiles
    private val libsPath = Configs.libsPath

    private val jacocoAgentPath = "$libsPath/jacocoagent.jar"
    private val jacocoCliPath = "$libsPath/jacococli.jar"
    private val kotlinRunTimePath = "$libsPath/kotlin-runtime-1.2.71.jar"
    private val kotlinStdLibPath = "$libsPath/kotlin-stdlib-1.8.10.jar"

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    private val timestamp = LocalDateTime.now().format(timeFormatter)

    private val jarPath = "$outputPath/jar/MyApplication.jar"
//    private val executionDataPath = "$outputPath/report/jacoco-MyApplication.exec"
    private val coverageReportPath = "$outputPath/report/coverage_report_$timestamp/"

    /**
     * Generate coverage info for the given test cases
     */
    fun generate(sootMethods: List<SootMethod>) {
        log.info("==========[Calculating fitness values]==========")

        // 1. Generate main class, and print it
        val mainClass = Utils.generateMainClass("KotMain", sootMethods)
        JasminPrinter.printJasminFile(mainClass)

        val sootMethod = sootMethods.firstOrNull()
        val sootClass = sootMethod?.declaringClass
        val className = sootClass?.name
        val relativePath = className?.replace(".", "/")

        val executionDataPath = "$outputPath/report/$relativePath/jacoco-${sootMethod?.name}.exec"

        // 2. Generate exec file
        generateExecFile(executionDataPath)

        // 3. Get coverage info
    }

    // TODO: fix to new
    private fun generateExecFile(executionDataPath: String) {
        log.info("Main class: $mainClass")
        log.info("Execution data path: $executionDataPath")

        val vmOption = "-javaagent:$jacocoAgentPath=includes=$includeFiles,excludes=CalleeTest,destfile=$executionDataPath,output=file"

        val isLinux = System.getProperty("os.name") == "Linux"

        val runtimeJars =
            if (isLinux) "$jarPath:$kotlinRunTimePath:$kotlinStdLibPath"
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

//    private fun getCoverageInfo() {
//        ReportGenerator("MyApplication", executionDataPath, classesFilePath).getSimpleInfo()
//    }

//    private fun generateHTMLReport() {
//        log.info("Generating HTML report: $coverageReportPath")
//
//        Files.createDirectory(Paths.get(coverageReportPath))
//
//        val args = arrayOf("java", "-jar",
//            jacocoCliPath,
//            "report", executionDataPath,
//            "--classfile=$sootOutputPath",
//            "--sourcefile=$sourceCodePath",
//            "--html", coverageReportPath
//        )
//
//        val ps = Runtime.getRuntime().exec(args)
//        LoggerUtils.logCommandOutput(log, ps)
//        ps.waitFor()
//    }

}
package org.kotsuite.ga.coverage

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader

class CoverageGenerator(private val jarPath: String, private val classesFilePath: String, private val executionDataPath: String) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val jacocoAgentPath = "../lib/jacocoagent.jar"
    private val jacocoCliPath = "../lib/jacococli.jar"
    private val kotlinRunTimePath = "../lib/kotlin-runtime-1.2.71.jar"
    private val kotlinStdLibPath = "../lib/kotlin-stdlib-1.8.10.jar"

    fun generateExecFile(mainClass: String, includeFiles: String) {
        log.info("Main class: $mainClass")
        log.info("Execution data path: $executionDataPath")

        val vmOption = "-javaagent:$jacocoAgentPath=includes=$includeFiles,excludes=CalleeTest,destfile=$executionDataPath,output=file"
        val runtimeJars = "$jarPath;$kotlinRunTimePath;$kotlinStdLibPath"
        val args = arrayOf("java", vmOption, "-cp", runtimeJars, mainClass)
        try {
            val ps = Runtime.getRuntime().exec(args)
            val stdInput = BufferedReader(InputStreamReader(ps.inputStream))
            val stdError = BufferedReader(InputStreamReader(ps.errorStream))

            log.info("Generating execution data file...")

            var s: String? = stdInput.readLine()
            while (s != null) {
                log.info(s)
                s = stdInput.readLine()
            }

            s = stdError.readLine()
            while (s != null) {
                log.error(s)
                s = stdError.readLine()
            }
            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }

    fun getCoverageInfo() {
        ReportGenerator("MyApplication", executionDataPath, classesFilePath).getSimpleInfo()
    }

}
package org.kotsuite.ga.coverage.fitness

import org.kotsuite.ga.Configs
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.coverage.ExecUtil
import org.kotsuite.ga.utils.LoggerUtils
import org.kotsuite.ga.utils.Utils.isLinux
import org.slf4j.LoggerFactory
import soot.SootClass
import soot.SootMethod
import java.nio.file.Files
import java.nio.file.Paths

class TestCaseFitness(
    private val jimpleTestClass: SootClass,
    private val testCase: TestCase,
    private val targetMethod: SootMethod,
    private val jimpleMainClass: SootClass,
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    // generated exec file,
    // e.g., `$MODULE_ROOT/kotsuite/exec/jacoco_TempCalleePrintHelloRound0_test_printHello_1`
    private val execDataPath = "${Configs.outputPath}/exec/"
    private val execDataFileName = "jacoco_${jimpleTestClass.shortName}_${testCase.testCaseName}.exec"
    private val execDataFile = "$execDataPath/$execDataFileName"

    fun generateTestCaseFitness() {
        generateExecFile()


        generateFitness()
    }

    /**
     * Generate exec file
     */
    private fun generateExecFile() {

        Files.createDirectories(Paths.get(execDataPath))

        val kotsuiteAgentArgs = "$jimpleTestClass.${testCase.testCaseName}"
        val jacocoAgentArgs = "includes=${Configs.includeFiles},destfile=$execDataFile,output=file"

        val classPaths = listOf(
            Configs.sootOutputPath,
            "${Configs.libsPath}/*",
        )
        val cpArg =
            if(isLinux()) classPaths.joinToString(":")
            else classPaths.joinToString(";")

        val vmOptions = listOf(
            "-javaagent:${Configs.kotsuiteAgentPath}=$kotsuiteAgentArgs",
            "-javaagent:${Configs.jacocoAgentPath}=$jacocoAgentArgs",
            "-cp", cpArg,
        )

        val command = arrayOf("java") + vmOptions + arrayOf(jimpleMainClass.name)
        try {
            log.info("Run command: ${command.joinToString(" ")}")

            val ps = Runtime.getRuntime().exec(command)
            LoggerUtils.logCommandOutput(log, ps)
            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }

    private fun generateFitness() {

        val execUtil = ExecUtil(
            "MyApplication",
            execDataFile,
            Configs.sootOutputPath,
            Configs.sourceCodePath,
        )

        val fitness = execUtil.getTestCaseFitness(targetMethod)
        testCase.fitness = fitness
    }
}
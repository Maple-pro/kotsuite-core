package org.kotsuite.ga.commands

import org.apache.logging.log4j.LogManager
import org.jacoco.core.runtime.AgentOptions
import org.kotsuite.Configs
import org.kotsuite.utils.*
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object TestRunner {
    private val log = LogManager.getLogger()

    private val jvmArgs = listOf(
        "-XX:+EnableDynamicAgentLoading",
        "-Xshare:off",
        "-Dnet.bytebuddy.experimental=true",
    )

    /**
     * Run test case or test suite
     *
     * @param command java command to run the test case or test suite
     * @return the test result
     */
    private fun runCommand(command: Array<String>): Boolean {
        return try {
            val res = execCommand(command)
            val psInput = res.first
            val psError = res.second

            log.logCommandOutput(psInput, psError)
            TestRunnerUtils.getTestResult(psInput)
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
            false
        }
    }

    /**
     * Run JVM with kotsuite agent and jacoco agent:
     *
     * - Collect coverage information to exec file
     * - Collect assert information to assert file
     *
     * @param jacocoAgentJarFile
     * @param kotsuiteAgentJarFile
     * @param jacocoAgentOptions
     * @param kotsuiteAgentOptions
     * @param mainClassName
     * @param classPath
     * @return the test result
     */
    fun runTestCaseWithKotSuiteAgentAndJacocoAgent(
        jacocoAgentJarFile: File,
        kotsuiteAgentJarFile: File,
        jacocoAgentOptions: AgentOptions,
        kotsuiteAgentOptions: KotSuiteAgentOptions,
        cliArguments: KotMainCliOptions,
        mainClassName: String,
        classPath: String,
    ): Boolean {
        val jacocoOptionsStr = getJacocoVMArgument(jacocoAgentOptions, jacocoAgentJarFile)
        val kotsuiteOptionsStr = kotsuiteAgentOptions.getVMArgument(kotsuiteAgentJarFile)
        val cliArgumentsArr = cliArguments.getCliArguments()

        val vmArguments = listOf(
            jacocoOptionsStr,
            kotsuiteOptionsStr,
            "-cp", classPath,
        )
        val javaArguments = vmArguments + arrayOf(mainClassName) + cliArgumentsArr
        val argumentFile = saveArgumentToFile(javaArguments)

        val command = arrayOf("java") + jvmArgs + listOf("@${argumentFile.absolutePath}")
        return runCommand(command)
    }

    /**
     * Run JVM with jacoco agent:
     *
     * - Collect coverage information to exec file
     *
     * @param jacocoAgentJarFile
     * @param jacocoAgentOptions
     * @param mainClassName
     * @param classPath
     * @return the test result
     */
    fun runTestSuiteWithJacocoAgent(
        jacocoAgentJarFile: File,
        jacocoAgentOptions: AgentOptions,
        cliArguments: KotMainCliOptions,
        mainClassName: String,
        classPath: String,
    ): Boolean {
        val jacocoOptionsStr = getJacocoVMArgument(jacocoAgentOptions, jacocoAgentJarFile)
        val vmArguments = listOf(
            jacocoOptionsStr,
            "-cp", classPath,
        )
        val cliArgumentsArr = cliArguments.getCliArguments()

        val javaArguments = vmArguments + arrayOf(mainClassName) + cliArgumentsArr
        val argumentFile = saveArgumentToFile(javaArguments)

        val command = arrayOf("java") + jvmArgs + listOf("@${argumentFile.absolutePath}")
        return runCommand(command)
    }

    private fun getJacocoVMArgument(jacocoAgentOptions: AgentOptions, jacocoAgentJarFile: File): String {
        return String.format("-javaagent:%s=%s", jacocoAgentJarFile, jacocoAgentOptions)
    }

    private fun saveArgumentToFile(arguments: List<String>): File {
        val dateTime = LocalDateTime.now()
        val argumentFile = File(Configs.getCommandFilePath("TestRunner", dateTime))
        argumentFile.writeText(arguments.joinToString(" ").replace("""\""", """\\"""))

        return argumentFile
    }
}
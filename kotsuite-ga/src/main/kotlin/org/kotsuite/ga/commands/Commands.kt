package org.kotsuite.ga.commands

import org.apache.logging.log4j.LogManager
import org.jacoco.core.runtime.AgentOptions
import org.kotsuite.utils.LoggerUtils
import java.io.File

object Commands {
    private val log = LogManager.getLogger()

    private fun runCommand(command: Array<String>) {
        try {
            val ps = Runtime.getRuntime().exec(command)
            LoggerUtils.logCommandOutput(log, ps)
            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
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
     */
    fun runJVMWithKotSuiteAgentAndJacocoAgent(
        jacocoAgentJarFile: File,
        kotsuiteAgentJarFile: File,
        jacocoAgentOptions: AgentOptions,
        kotsuiteAgentOptions: KotSuiteAgentOptions,
        mainClassName: String,
        classPath: String,
    ) {
        val jacocoOptionsStr = getJacocoVMArgument(jacocoAgentOptions, jacocoAgentJarFile)
        val kotsuiteOptionsStr = kotsuiteAgentOptions.getVMArgument(kotsuiteAgentJarFile)

        val vmArguments = listOf(
            jacocoOptionsStr,
            kotsuiteOptionsStr,
            "-cp", classPath,
        )
        val command = arrayOf("java") + vmArguments + arrayOf(mainClassName)
        runCommand(command)
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
     */
    fun runJVMWithJacocoAgent(
        jacocoAgentJarFile: File,
        jacocoAgentOptions: AgentOptions,
        mainClassName: String,
        classPath: String,
    ) {
        val jacocoOptionsStr = getJacocoVMArgument(jacocoAgentOptions, jacocoAgentJarFile)
        val vmArguments = listOf(
            jacocoOptionsStr,
            "-cp", classPath,
        )
        val command = arrayOf("java") + vmArguments + arrayOf(mainClassName)
        runCommand(command)
    }

    private fun getJacocoVMArgument(jacocoAgentOptions: AgentOptions, jacocoAgentJarFile: File): String {
        return String.format("-javaagent:%s=%s", jacocoAgentJarFile, jacocoAgentOptions)
    }
}
package org.kotsuite.ga.coverage

import org.kotsuite.ga.Configs
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.utils.LoggerUtils
import org.kotsuite.ga.utils.Utils
import org.slf4j.LoggerFactory

object JacocoUtils {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Generate exec file with kotsuite agent
     *
     * @param classesPath classes path where exists the classes
     * @param execDataFile the generated exec file path
     * @param testClassName the class name needs to be inserted
     * @param testCaseName the test case needs to be inserted
     * @param mainClassName the main class of the classes
     */
    fun generateExecFileWithKotsuiteAgent(
        classesPath: String,
        execDataFile: String,
        testClassName: String,
        testCaseName: String,
        mainClassName: String,
        generateAssert: Boolean = false,
        testCase: TestCase? = null,
    ) {
//        log.info("Generating exec file: $testClassName.$testCaseName")

        val kotsuiteAgentArgs =
            if (testClassName != "" && testCaseName != "") "$testClassName.$testCaseName"
            else ""
        val jacocoAgentArgs = "includes=${Configs.includeFiles},destfile=$execDataFile,output=file"

        val classPaths = listOf(
            classesPath,
            "${Configs.libsPath}/*",
        ) + Configs.dependencyClassPaths

        val cpArg =
            if(Utils.isLinux()) classPaths.joinToString(":")
            else classPaths.joinToString(";")

        val vmOptions = listOf(
            "-javaagent:${Configs.kotsuiteAgentPath}=$kotsuiteAgentArgs",
            "-javaagent:${Configs.jacocoAgentPath}=$jacocoAgentArgs",
            "-cp", cpArg,
        )

        val command = arrayOf("java") + vmOptions + arrayOf(mainClassName)
//        log.info("Run command: ${command.joinToString(" ")}")
        try {
            val ps = Runtime.getRuntime().exec(command)

            if (generateAssert && testCase != null) {
                testCase.generateAssertByProcess(ps)
            }
            LoggerUtils.logCommandOutput(log, ps, Configs.showDebugLog)

            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }
}
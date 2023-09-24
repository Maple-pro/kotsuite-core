package org.kotsuite.ga.coverage

import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.utils.LoggerUtils
import java.io.File

object JacocoUtils {

    private val log = LogManager.getLogger()

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

        val cpArg = classPaths.joinToString(File.pathSeparator)

        val vmOptions = listOf(
//            "-javaagent:${Configs.KOTSUITE_AGENT_PATH}=$kotsuiteAgentArgs",
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
            LoggerUtils.logCommandOutput(log, ps)

            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }
}
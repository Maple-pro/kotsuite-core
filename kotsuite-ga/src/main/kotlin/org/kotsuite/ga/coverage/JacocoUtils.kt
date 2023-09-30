package org.kotsuite.ga.coverage

import org.apache.logging.log4j.LogManager
import org.jacoco.core.runtime.AgentOptions
import org.kotsuite.Configs
import org.kotsuite.ga.commands.Commands
import org.kotsuite.ga.commands.KotSuiteAgentOptions
import java.io.File

object JacocoUtils {

    private val log = LogManager.getLogger()

    /**
     * Generate exec file with kotsuite agent,
     * for test case or population
     *
     * @param mainClassName the main class of the classes
     * @param testClassName the class name needs to be inserted
     * @param testCaseName the test case needs to be inserted
     * @param targetClass the target class needs to be inserted
     * @param targetMethod the target method needs to be inserted
     * @param targetMethodDesc the target method description needs to be inserted
     * @param execDataFile the generated exec file path
     * @param assertFile the generated assert file path
     * @param classesPath classes path where exists the classes
     */
    fun generateTestCaseExecFile(
        mainClassName: String,
        testClassName: String,
        testCaseName: String,
        targetClass: String,
        targetMethod: String,
        targetMethodDesc: String,
        execDataFile: String,
        assertFile: String,
        classesPath: String,
    ) {
        val testMethodDesc = "()V"

        val jacocoAgentOptions = AgentOptions()
        with(jacocoAgentOptions) {
            includes = Configs.includeFiles
            destfile = execDataFile
            output = AgentOptions.OutputMode.file
        }

        val kotSuiteAgentOptions = KotSuiteAgentOptions()
        with(kotSuiteAgentOptions) {
            setInsertCall(true)
            setCollectAssert(true)
            setOutputFile(assertFile)
            setMainClass(mainClassName)
            setTestClass(testClassName)
            setTestMethod(testCaseName)
            setTestMethodDesc(testMethodDesc)
            setTargetClass(targetClass)
            setTargetMethod(targetMethod)
            setTargetMethodDesc(targetMethodDesc)
        }

        val cp = listOf(classesPath, "${Configs.libsPath}/classpath/*",) + Configs.dependencyClassPaths
        val cpStr = cp.joinToString(File.pathSeparator)

        Commands.runJVMWithKotSuiteAgentAndJacocoAgent(
            File(Configs.jacocoAgentPath),
            File(Configs.kotsuiteAgentPath),
            jacocoAgentOptions,
            kotSuiteAgentOptions,
            mainClassName,
            cpStr,
        )

    }

    /**
     * Generate final whole solution exec file,
     * only run with jacoco agent
     *
     * @param classesPath
     * @param execDataFile
     * @param mainClassName
     */
    fun generateFinalWholeSolutionExecFile(
        mainClassName: String,
        execDataFile: String,
        classesPath: String,
    ) {
        val jacocoAgentOptions = AgentOptions()
        with(jacocoAgentOptions) {
            includes = Configs.includeFiles
            destfile = execDataFile
            output = AgentOptions.OutputMode.file
        }
        val cp = listOf(classesPath, "${Configs.libsPath}/classpath/*",) + Configs.dependencyClassPaths
        val cpStr = cp.joinToString(File.pathSeparator)
        Commands.runJVMWithJacocoAgent(
            File(Configs.jacocoAgentPath),
            jacocoAgentOptions,
            mainClassName,
            cpStr,
        )
    }

    fun generatePopulationExecFile(
        mainClassName: String,
        testClassName: String,
        execDataFile: String,
        classesPath: String,
    ) {
        generateTestCaseExecFile(
            mainClassName,
            testClassName,
            "*",
            "",
            "",
            "",
            execDataFile,
            "",
            classesPath,
        )
    }
}
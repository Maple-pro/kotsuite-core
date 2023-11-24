package org.kotsuite.ga.coverage

import org.apache.logging.log4j.LogManager
import org.jacoco.core.runtime.AgentOptions
import org.kotsuite.Configs
import org.kotsuite.analysis.Dependency
import org.kotsuite.ga.commands.TestRunner
import org.kotsuite.ga.commands.KotMainCliOptions
import org.kotsuite.ga.commands.KotSuiteAgentOptions
import org.kotsuite.ga.commands.TestResult
import org.kotsuite.ga.solution.WholeSolution
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
     * @return test case running result
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
    ): TestResult {
        val jacocoAgentOptions = AgentOptions()
        with(jacocoAgentOptions) {
            includes = Configs.includeFiles
            destfile = execDataFile
            output = AgentOptions.OutputMode.file
        }

        val kotSuiteAgentOptions = KotSuiteAgentOptions()
        with(kotSuiteAgentOptions) {
            setCollectAssert(true)
            setOutputFile(assertFile)
            setTestClass(testClassName)
            setTestMethod(testCaseName)
            setTargetClass(targetClass)
            setTargetMethod(targetMethod)
            setTargetMethodDesc(targetMethodDesc)
        }

        val kotMainCliOptions = KotMainCliOptions()
        with(kotMainCliOptions) {
            setClass(listOf(testClassName))
            setMethod(testCaseName)
        }

        val cp = listOf(classesPath) + // the target class files path of the project, e.g., sootOutput/
                Dependency.getClassPath() + // the classpath jars from libs/
                Dependency.getTestFramework() + // the test framework jars from libs/
                Dependency.getTestDependencies() + // the test dependencies jars from libs/
                Configs.dependencyClassPaths // the dependency class paths of the project

        val cpStr = cp.joinToString(File.pathSeparator)

        return TestRunner.runTestCaseWithKotSuiteAgentAndJacocoAgent(
            File(Configs.jacocoAgentPath),
            File(Configs.kotsuiteAgentPath),
            jacocoAgentOptions,
            kotSuiteAgentOptions,
            kotMainCliOptions,
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
        wholeSolution: WholeSolution,
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

        val testClasses = wholeSolution.classSolutions.map { it.testClass.getFullTestClassName() }
        val kotMainCliOptions = KotMainCliOptions()
        with(kotMainCliOptions) {
            setClass(testClasses)
            setMethod("*")
        }

        val cp = listOf(classesPath) + // the target class files path of the project, e.g., sootOutput/
                Dependency.getClassPath() + // the classpath jars from libs/
                Dependency.getTestFramework() + // the test framework jars from libs/
                Dependency.getTestDependencies() + // the test dependencies jars from libs/
                Configs.dependencyClassPaths // the dependency class paths of the project

        val cpStr = cp.joinToString(File.pathSeparator)

        TestRunner.runTestSuiteWithJacocoAgent(
            File(Configs.jacocoAgentPath),
            jacocoAgentOptions,
            kotMainCliOptions,
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
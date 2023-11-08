package org.kotsuite.ga.coverage.fitness

import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.coverage.ExecResolver
import org.kotsuite.ga.coverage.JacocoUtils
import org.kotsuite.utils.ASMUtils.getMethodDescription
import soot.SootClass
import soot.SootMethod

class TestCaseFitness(
    private val jimpleTestClass: SootClass,
    private val testCase: TestCase,
    private val targetMethod: SootMethod,
    private val assertFilePath: String,
) {

    private val log = LogManager.getLogger()

    // generated exec file,
    // e.g., `$MODULE_ROOT/kotsuite/exec/jacoco_TempCalleePrintHelloRound0_test_printHello_1`
    private val execDataFile = Configs.getExecFilePath(jimpleTestClass.name, testCase.testCaseName)

    fun generateTestCaseFitness(): String {
        val targetMethod = testCase.targetMethod
        val targetClass = targetMethod.declaringClass
        val targetMethodDesc = testCase.targetMethod.getMethodDescription() // Util: create method desc for a soot method
        val testCaseResult = JacocoUtils.generateTestCaseExecFile(
            Configs.mainClass,
            jimpleTestClass.name,
            testCase.testCaseName,
            targetClass.name,
            targetMethod.name,
            targetMethodDesc,
            execDataFile,
            assertFilePath,
            Configs.sootOutputPath,
        )
        testCase.testResult = testCaseResult // Get the running result of the test case
        generateFitness()

        return execDataFile
    }

    private fun generateFitness() {

        val execResolver = ExecResolver(
            "MyApplication",
            execDataFile,
            Configs.sootOutputPath,
            Configs.sourceCodePath,
        )

        val fitness = execResolver.getTargetMethodFitness(targetMethod)
        testCase.fitness = fitness
    }
}
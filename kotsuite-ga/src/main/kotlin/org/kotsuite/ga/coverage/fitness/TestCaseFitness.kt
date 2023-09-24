package org.kotsuite.ga.coverage.fitness

import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.coverage.ExecResolver
import org.kotsuite.ga.coverage.JacocoUtils
import soot.SootClass
import soot.SootMethod

class TestCaseFitness(
    private val jimpleTestClass: SootClass,
    private val testCase: TestCase,
    private val targetMethod: SootMethod,
    private val jimpleMainClass: SootClass,
) {

    private val log = LogManager.getLogger()

    // generated exec file,
    // e.g., `$MODULE_ROOT/kotsuite/exec/jacoco_TempCalleePrintHelloRound0_test_printHello_1`
    private val execDataFile = Configs.getExecFilePath(jimpleTestClass.name, testCase.testCaseName)

    fun generateTestCaseFitness() {
        JacocoUtils.generateExecFileWithKotsuiteAgent(
            Configs.sootOutputPath,
            execDataFile,
            jimpleTestClass.name,
            testCase.testCaseName,
            jimpleMainClass.name,
            generateAssert = true,
            testCase,
        )
        generateFitness()
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
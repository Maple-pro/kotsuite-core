package org.kotsuite.ga.solution

import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.commands.TestResult
import org.kotsuite.ga.coverage.fitness.Fitness
import soot.SootMethod

data class MethodSolution(
    val targetMethod: SootMethod,
    val testCases: List<TestCase>,
    var fitness: Fitness? = null
) {
    fun getSuccessfulMethodSolution(): MethodSolution {
        val successfulTestCases = testCases.filter { it.testResult == TestResult.SUCCESSFUL }
        return MethodSolution(targetMethod, successfulTestCases)
    }

    fun exceptCrashedMethodSolution(): MethodSolution {
        val exceptCrashedTestCases = testCases.filter { it.testResult != TestResult.CRASHED }
        return MethodSolution(targetMethod, exceptCrashedTestCases)
    }
}
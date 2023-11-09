package org.kotsuite.ga.solution

import org.kotsuite.ga.chromosome.TestCase
import soot.SootMethod

data class MethodSolution(
    val targetMethod: SootMethod,
    val testCases: List<TestCase>,
) {
    fun getSuccessfulMethodSolution(): MethodSolution {
        val successfulTestCases = testCases.filter { it.testResult }
        return MethodSolution(targetMethod, successfulTestCases)
    }
}
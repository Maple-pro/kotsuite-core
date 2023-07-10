package org.kotsuite.ga.solution

import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.chromosome.TestClass
import soot.SootMethod

data class MethodSolution(
    val targetMethod: SootMethod,
    val testCases: List<TestCase>,
)
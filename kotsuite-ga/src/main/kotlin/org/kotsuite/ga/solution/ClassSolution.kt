package org.kotsuite.ga.solution

import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.coverage.fitness.Fitness
import soot.SootClass

class ClassSolution(
    val targetClass: SootClass,
    val testClass: TestClass,
    val methodSolutions: List<MethodSolution>,
    var fitness: Fitness? = null,
) {
    init {
        testClass.testCases = methodSolutions.flatMap { it.testCases }.toMutableList()
    }

    fun getSuccessfulClassSolution(): ClassSolution {
        val successfulMethodSolutions = methodSolutions.map { it.getSuccessfulMethodSolution() }
        return ClassSolution(targetClass, testClass, successfulMethodSolutions)
    }
}
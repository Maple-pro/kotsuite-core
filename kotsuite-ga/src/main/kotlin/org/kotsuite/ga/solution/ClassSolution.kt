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

    fun getSuccessfulClassSolution(): ClassSolution? {
        val successfulMethodSolutions = methodSolutions.mapNotNull { it.getSuccessfulMethodSolution() }

        if (successfulMethodSolutions.isEmpty()) {
            return null
        }

        val newTestClass = TestClass(testClass.testClassName + "Success", testClass.packageName, testClass.round)
        return ClassSolution(targetClass, newTestClass, successfulMethodSolutions)
    }

    fun getFailedClassSolution(): ClassSolution? {
        val failedMethodSolutions = methodSolutions.mapNotNull { it.getFailedMethodSolution() }

        if (failedMethodSolutions.isEmpty()) {
            return null
        }

        val newTestClass = TestClass(testClass.testClassName + "Failed", testClass.packageName,testClass.round)
        return ClassSolution(targetClass, newTestClass, failedMethodSolutions)
    }

    fun exceptCrashedClassSolution(): ClassSolution? {
        val exceptCrashedMethodSolutions = methodSolutions.mapNotNull { it.exceptCrashedMethodSolution() }

        if (exceptCrashedMethodSolutions.isEmpty()) {
            return null
        }
        return ClassSolution(targetClass, testClass, exceptCrashedMethodSolutions)
    }
}
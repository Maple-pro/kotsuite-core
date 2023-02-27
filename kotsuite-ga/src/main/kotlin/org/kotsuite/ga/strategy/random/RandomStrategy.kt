package org.kotsuite.ga.strategy.random

import org.kotsuite.ga.GAStrategy
import org.kotsuite.ga.chromosome.TestCase
import soot.SootMethod

class RandomStrategy: GAStrategy() {

    /**
     * Generate test cases for the target method.
     * This strategy only generates one test case.
     * And the generated test case only contains two actions: CONSTRUCTOR and METHOD_CALL.
     * The first action build a new object of the targetClass,
     * and the second action issues a call to the target method on the object.
     * @param targetMethod the target method needs to be generated
     */
    override fun generateTestCasesForMethod(targetMethod: SootMethod): List<TestCase>{
        // TODO: Generate TestCases for method
        val testCases = ArrayList<TestCase>()

        val targetClass = targetMethod.declaringClass

        println(targetMethod.signature)



        return testCases
    }

}
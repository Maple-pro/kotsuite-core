package org.kotsuite.ga.strategy.random

import org.kotsuite.ga.GAStrategy
import org.kotsuite.ga.chromosome.Action
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.chromosome.type.ActionType
import soot.SootMethod

class RandomStrategy: GAStrategy() {

    /**
     * Generate test cases for the target method.
     * This strategy only generates one test case.
     * And the generated test case only contains two actions: CONSTRUCTOR and METHOD_CALL.
     * The first action build a new object of the targetClass,
     * and the second action issues a call to the target method on the object.
     *
     * @param targetMethod the target method needs to be generated
     */
    override fun generateTestCasesForMethod(targetMethod: SootMethod): List<TestCase>{
        val testCases = ArrayList<TestCase>()

        val testCaseName = "test_${targetMethod.name}_1"

        testCases.add(generateTestCaseForMethod(targetMethod, testCaseName))

        return testCases
    }

    private fun generateTestCaseForMethod(targetMethod: SootMethod, testCaseName: String): TestCase {
        // TODO: Generate TestCase for method
        val testCase = TestCase(testCaseName)
        val targetClass = targetMethod.declaringClass

        val constructorAction = Action(ActionType.CONSTRUCTOR)
        // Note: constructor in Action can be RefType.v(targetClass)

        val methodCallAction = Action(ActionType.METHOD_CALL)

        testCase.actions.add(constructorAction)
        testCase.actions.add(methodCallAction)

        return testCase
    }

}
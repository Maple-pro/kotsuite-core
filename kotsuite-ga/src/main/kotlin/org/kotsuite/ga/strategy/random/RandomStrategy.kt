package org.kotsuite.ga.strategy.random

import org.kotsuite.ga.GAStrategy
import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.chromosome.type.ActionType
import org.kotsuite.ga.chromosome.type.ParameterType
import soot.*
import soot.dava.internal.javaRep.DIntConstant
import soot.jimple.DoubleConstant
import soot.jimple.IntConstant
import kotlin.collections.ArrayList
import kotlin.random.Random

object RandomStrategy: GAStrategy() {

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

        for (i in 1..10) {
            val testCaseName = "test_${targetMethod.name}_$i"

            testCases.add(generateTestCaseForMethod(targetMethod, testCaseName))
        }

        return testCases
    }

    private fun generateTestCaseForMethod(targetMethod: SootMethod, testCaseName: String): TestCase {
        val testCase = TestCase(testCaseName)
        val targetClass = targetMethod.declaringClass

        var valueIndex = 0

        // Add constructor action
        val constructorAction = Action(ActionType.CONSTRUCTOR)
        constructorAction.variable = Variable("obj", targetClass.type, null)
        val constructor = getConstructor(targetClass)
        constructorAction.constructor = constructor
        valueIndex = dealWithMethodCallParams(testCase, constructorAction, constructor, valueIndex)

        // Add method call action
        val methodCallAction = Action(ActionType.METHOD_CALL)
        methodCallAction.variable = constructorAction.variable
        methodCallAction.method = targetMethod
        valueIndex = dealWithMethodCallParams(testCase, methodCallAction, targetMethod, valueIndex)

        testCase.actions.add(constructorAction)
        testCase.actions.add(methodCallAction)

        return testCase
    }

    private fun dealWithMethodCallParams(testCase: TestCase, action: Action, method: SootMethod, valueIndex: Int): Int {
        // Candidate values
        val intCandidates = listOf(-1000, -100, -10 , -3, -2, -1, 0, 1, 2, 3, 10, 100, 1000)
        val booleanCandidates = listOf(0, 1)
        val doubleCandidates = listOf(-1000.0, -100.0, -10.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 10.0, 100.0, 1000.0)

        var returnValueIndex = valueIndex
        method.parameterTypes.forEach {
            if (it is PrimType) {
                val parameter = Parameter(ParameterType.BUILTIN_TYPE)
                parameter.primType = it
                parameter.valueIndex = returnValueIndex
                returnValueIndex++
                action.parameters.add(parameter)

                val value = when (parameter.primType) {
                    is IntType ->
                        IntConstant.v(intCandidates[Random.nextInt(intCandidates.size)])
                    is BooleanType ->
                        DIntConstant.v(booleanCandidates[Random.nextInt(booleanCandidates.size)], BooleanType.v())
                    is DoubleType ->
                        DoubleConstant.v(doubleCandidates[Random.nextInt(doubleCandidates.size)])
                    else ->
                        null
                }
                if (value != null) {
                    testCase.values.add(value)
                }
            } else if (it is RefType) {
                val parameter = Parameter(ParameterType.VARIABLE)
                parameter.variable = Variable("var_${it.sootClass.shortName}", it, null)
                action.parameters.add(parameter)
            } else if (it is ArrayType) {
                val parameter = Parameter(ParameterType.VARIABLE)
                parameter.variable = Variable("var_array_int", null, it)
                action.parameters.add(parameter)
            }
        }

        return returnValueIndex
    }

    private fun getConstructor(sootClass: SootClass): SootMethod {
        return try {
            sootClass.getMethod("void <init>()")
        } catch (ex: RuntimeException) {
            sootClass.getMethodByName("<init>")
        }
    }

}
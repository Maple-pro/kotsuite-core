package org.kotsuite.ga.strategy.random

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type
import org.kotsuite.ga.GAStrategy
import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.chromosome.type.ActionType
import org.kotsuite.ga.chromosome.type.ParameterType
import soot.BooleanType
import soot.IntType
import soot.PrimType
import soot.SootClass
import soot.SootMethod
import soot.dava.internal.javaRep.DIntConstant
import soot.jimple.IntConstant
import soot.jimple.NullConstant
import java.util.*
import kotlin.collections.ArrayList

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

        val testCaseName = "test_${targetMethod.name}_1"

        testCases.add(generateTestCaseForMethod(targetMethod, testCaseName))

        return testCases
    }

    private fun generateTestCaseForMethod(targetMethod: SootMethod, testCaseName: String): TestCase {
        // TODO: Generate TestCase for method
        val testCase = TestCase(testCaseName)
        val targetClass = targetMethod.declaringClass

        var valueIndex = 0

        // Add constructor action
        val constructorAction = Action(ActionType.CONSTRUCTOR)
        constructorAction.variable = Variable("obj")
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
        var returnValueIndex = valueIndex
        method.parameterTypes.forEach {
            if (it is PrimType) {
                val parameter = Parameter(ParameterType.BUILTIN_TYPE)
                parameter.primType = it
                parameter.valueIndex = returnValueIndex
                returnValueIndex++
                action.parameters.add(parameter)

                val value = when (parameter.primType) {
                    is IntType -> IntConstant.v(0)
                    is BooleanType -> DIntConstant.v(0, BooleanType.v())
                    else -> null
                }
                if (value != null) {
                    testCase.values.add(value)
                }
            } else {
                val parameter = Parameter(ParameterType.VARIABLE)
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
package org.kotsuite.ga.strategy.random

import org.kotsuite.analysis.Analyzer
import org.kotsuite.ga.strategy.Strategy
import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.chromosome.action.Action
import org.kotsuite.ga.chromosome.action.ConstructorAction
import org.kotsuite.ga.chromosome.action.MethodCallAction
import org.kotsuite.ga.chromosome.parameter.*
import org.kotsuite.ga.chromosome.value.Value
import org.kotsuite.ga.solution.ClassSolution
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.ga.solution.WholeSolution
import org.kotsuite.ga.utils.SootUtils
import soot.*
import kotlin.collections.ArrayList

object RandomStrategy: Strategy {
    override fun generateWholeSolution(): WholeSolution {
        val classSolutions = Analyzer.classes.map {
            generateClassSolution(it)
        }

        return WholeSolution(classSolutions)
    }

    private fun generateClassSolution(targetClass: SootClass): ClassSolution {
        // Generate TestClass for target class
        val testClassName = "${targetClass.shortName}Test"
        val testClass = TestClass(testClassName, targetClass.packageName)

        val methodSolutions = targetClass.methods
            .filter { filterMethod(it) }
            .map { generateMethodSolution(it) }

        return ClassSolution(targetClass, testClass, methodSolutions)
    }

    private fun generateMethodSolution(targetMethod: SootMethod): MethodSolution {
        return MethodSolution(targetMethod, generateTestCasesForMethod(targetMethod))
    }

    private fun filterMethod(sootMethod: SootMethod): Boolean {
        return !(sootMethod.subSignature.equals("void <init>()") || sootMethod.name.equals("<init>"))
    }

    /**
     * Generate test cases for the target method.
     * This strategy only generates one test case.
     * And the generated test case only contains two actions: CONSTRUCTOR and METHOD_CALL.
     * The first action build a new object of the targetClass,
     * and the second action issues a call to the target method on the object.
     *
     * @param targetMethod the target method needs to be generated
     */
    fun generateTestCasesForMethod(targetMethod: SootMethod): List<TestCase>{
        val testCases = ArrayList<TestCase>()

        val testCaseNum = 10

        for (i in 1..testCaseNum) {
            val testCaseName = "test_${targetMethod.name}_$i"

            testCases.add(generateTestCaseForMethod(targetMethod, testCaseName))
        }

        return testCases
    }

    private fun generateTestCaseForMethod(targetMethod: SootMethod, testCaseName: String): TestCase {
        val testCase = TestCase(testCaseName, 0)
        val targetClass = targetMethod.declaringClass

        val constructorAction = generateConstructorAction(testCase, targetClass)
        testCase.actions.add(constructorAction)

        val methodCallAction = generateMethodCallAction(testCase, constructorAction.variable, targetMethod)
        testCase.actions.add(methodCallAction)

        return testCase
    }

    private fun generateConstructorAction(testCase: TestCase, targetClass: SootClass): ConstructorAction {
        val variableName = "${targetClass.shortName}_obj"
        val variable = Variable(variableName, targetClass.type)
        val constructor = SootUtils.getConstructor(targetClass)

        val constructorAction = ConstructorAction(variable, constructor, mutableListOf())
        dealActionParameters(testCase, constructorAction, constructor)

        return constructorAction
    }

    private fun generateMethodCallAction(
        testCase: TestCase,
        variable: Variable,
        targetMethod: SootMethod
    ): MethodCallAction {

        val methodCallAction = MethodCallAction(variable, targetMethod, mutableListOf())
        dealActionParameters(testCase, methodCallAction, targetMethod)

        return methodCallAction
    }

    private fun dealActionParameters(
        testCase: TestCase,
        action: Action,
        method: SootMethod,
    ) {

        var valueIndex = testCase.values.size

        method.parameterTypes.forEach {
            val value: Value?
            val parameter: Parameter?

            when (it) {
                is PrimType -> {
                    value = ValueGenerator.generatePrimValue(it)
                    parameter = PrimParameter(it, valueIndex)
                }
                is RefType -> {
                    if (it == RefType.v("java.lang.String")) {
                        value = ValueGenerator.generateStringValue()
                        parameter = StringParameter(valueIndex)
                    } else {
                        value = null

                        val constructorAction = generateConstructorAction(testCase, it.sootClass)
                        testCase.actions.add(constructorAction)

                        parameter = RefTypeParameter(constructorAction.variable)
                    }
                }
                is ArrayType -> {
                    value = ValueGenerator.generateArrayValue(it)
                    parameter = ArrayParameter(it, valueIndex)
                }
                else -> {
                    value = null
                    parameter = null
                }
            }

            if (value != null) {
                testCase.values.add(value)
                valueIndex++
            }
            if (parameter != null) {
                action.parameters.add(parameter)
            }
        }
    }
}
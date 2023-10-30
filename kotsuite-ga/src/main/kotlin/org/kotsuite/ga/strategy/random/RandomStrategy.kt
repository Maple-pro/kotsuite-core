package org.kotsuite.ga.strategy.random

import org.apache.logging.log4j.LogManager
import org.kotsuite.CommonClassConstants
import org.kotsuite.ga.strategy.Strategy
import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.chromosome.action.*
import org.kotsuite.ga.chromosome.parameter.*
import org.kotsuite.ga.chromosome.value.ChromosomeValue
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.soot.SootUtils.getConstructor
import org.kotsuite.soot.TestDoubleType
import soot.*
import kotlin.collections.ArrayList

object RandomStrategy: Strategy() {
    private val log = LogManager.getLogger()

    override fun generateMethodSolution(targetMethod: SootMethod, targetClass: SootClass): MethodSolution {
        return MethodSolution(targetMethod, generateTestCasesForMethod(targetMethod))
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

    /**
     * Generate test case for method
     *
     * TODO:
     * 1. Generate mock action: add mock action type
     * 2. Code reuse
     *
     * @param targetMethod
     * @param testCaseName
     * @return
     */
    private fun generateTestCaseForMethod(targetMethod: SootMethod, testCaseName: String): TestCase {
        val testCase = TestCase(testCaseName, targetMethod)
        val targetClass = targetMethod.declaringClass
        val variable: Variable

//        val constructorAction = generateConstructorAction(testCase, targetClass)
//        testCase.actions.add(constructorAction)
//        variable = constructorAction.variable

        // 初始化实例对象
        when (val initializationType = targetClass.getInitializationType()) {
            // 使用构造函数初始化
            InitializationType.CONSTRUCTOR -> {
                val constructorAction = generateConstructorAction(testCase, targetClass)
                testCase.actions.add(constructorAction)
                variable = constructorAction.variable
            }
            // 使用 mock 或 spy 初始化对象，并随机 mock 对象的行为
            InitializationType.TEST_DOUBLE -> {
                val testDoubleType = targetClass.getTestDoubleType()
                val mockObjectAction = generateMockObjectAction(testDoubleType, targetClass)
                testCase.actions.add(mockObjectAction)
                variable = mockObjectAction.variable

                // TODO: 可能是函数列表或属性？
                val mockWhenMethod = getMockWhenMethod(targetClass, targetMethod)
                val mockWhenAction = generateMockWhenAction(variable, mockWhenMethod)
                testCase.actions.add(mockWhenAction)
            }
        }

        val methodCallAction = generateMethodCallAction(testCase, variable, targetMethod)
        testCase.actions.add(methodCallAction)

        return testCase
    }

    private fun generateConstructorAction(testCase: TestCase, targetClass: SootClass): ConstructorAction {
        val variableName = "${targetClass.shortName}_obj"
        val variable = Variable(variableName, targetClass.type)
        val constructor = targetClass.getConstructor()

        val constructorAction = ConstructorAction(variable, constructor!!, mutableListOf())
        dealActionParameters(testCase, constructorAction, constructor)

        return constructorAction
    }

    private fun generateMockObjectAction(testDoubleType: TestDoubleType, targetClass: SootClass): TestDoubleAction {
        val variableName = when (testDoubleType) {
            TestDoubleType.MOCKITO_MOCK -> {
                "${targetClass.shortName}_mockito_mock_obj"
            }
            TestDoubleType.MOCKITO_SPY -> {
                "${targetClass.shortName}_mockito_spy_obj"
            }
            TestDoubleType.JMOCKK_MOCK -> {
                "${targetClass.shortName}_mockk_mock_obj"
            }
            TestDoubleType.JMOCKK_SPY -> {
                "${targetClass.shortName}_mockk_spy_obj"
            }
        }

        val variable = Variable(variableName, targetClass.type)
        return TestDoubleAction(variable, testDoubleType, targetClass, mutableListOf())
    }

    /**
     * Generate mock when action
     * 问题：函数参数？函数返回的值？
     *
     * @param variable
     * @param mockMethod
     * @return
     */
    private fun generateMockWhenAction(variable: Variable, mockMethod: SootMethod): MockWhenAction {
        TODO()
    }

    /**
     * Get mock when method
     * 问题：如何判断mock那些函数？
     *
     * @param targetClass
     * @param targetMethod
     * @return
     */
    private fun getMockWhenMethod(targetClass: SootClass, targetMethod: SootMethod): SootMethod {
        TODO()
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
            val value: ChromosomeValue?
            val parameter: Parameter?

            when (it) {
                is PrimType -> {
                    value = ValueGenerator.generatePrimValue(it)
                    parameter = PrimParameter(it, valueIndex)
                }
                is RefType -> {
                    if (it == RefType.v(CommonClassConstants.string_class_name)) {
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

    /**
     * TODO
     * Get initialization type, strategy:
     *
     * @return
     */
    private fun SootClass.getInitializationType(): InitializationType {
        return InitializationType.TEST_DOUBLE
    }

    /**
     * TODO
     * Get test double type
     *
     * @return
     */
    private fun SootClass.getTestDoubleType(): TestDoubleType {
        return TestDoubleType.MOCKITO_SPY
    }

    @Override
    override fun toString() = "Random Strategy"
}
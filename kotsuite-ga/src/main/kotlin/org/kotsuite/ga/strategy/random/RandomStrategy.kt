package org.kotsuite.ga.strategy.random

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.strategy.Strategy
import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.chromosome.action.*
import org.kotsuite.ga.chromosome.parameter.*
import org.kotsuite.ga.chromosome.value.ChromosomeValue
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.utils.soot.SootUtils.getConstructor
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
        var variable: Variable

//        val constructorAction = generateConstructorAction(testCase, targetClass)
//        testCase.actions.add(constructorAction)
//        variable = constructorAction.variable

        // 初始化实例对象
        when (val initializationType = getInitializationType(targetClass)) {
            // 使用构造函数初始化
            InitializationType.CONSTRUCTOR -> {
                val constructorAction = generateConstructorAction(testCase, targetClass)
                testCase.actions.add(constructorAction)
                variable = constructorAction.variable
            }
            // 使用 mock 或 spy 初始化对象，并随机 mock 对象的行为
            InitializationType.MOCK, InitializationType.SPY -> {
                val mockObjectAction = generateMockObjectAction(initializationType, targetClass)
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

    private fun generateMockObjectAction(initializationType: InitializationType, targetClass: SootClass): MockObjectAction {
        return when (initializationType) {
            InitializationType.MOCK -> {
                val variableName = "${targetClass.shortName}_mock_obj"
                val variable = Variable(variableName, targetClass.type)
                MockObjectAction(variable, initializationType, targetClass, mutableListOf())
            }
            InitializationType.SPY -> {
                val variableName = "${targetClass.shortName}_spy_obj"
                val variable = Variable(variableName, targetClass.type)
                MockObjectAction(variable, initializationType, targetClass, mutableListOf())
            }

            InitializationType.CONSTRUCTOR -> {
                log.error("Initialization type can not be CONSTRUCTOR")
                throw IllegalArgumentException("Initialization type can not be CONSTRUCTOR")
            }
        }
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

    /**
     * TODO
     * Get initialization type
     * 问题：用什么策略来决定初始化类型？
     *
     * @param sootClass
     * @return
     */
    private fun getInitializationType(sootClass: SootClass): InitializationType {
        return InitializationType.MOCK
    }

    @Override
    override fun toString() = "Random Strategy"
}
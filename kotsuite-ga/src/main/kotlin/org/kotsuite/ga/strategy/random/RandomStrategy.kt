package org.kotsuite.ga.strategy.random

import org.apache.logging.log4j.LogManager
import org.kotsuite.CommonClassConstants
import org.kotsuite.ga.strategy.Strategy
import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.chromosome.action.*
import org.kotsuite.ga.chromosome.parameter.*
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.soot.MockWhenActionType
import org.kotsuite.soot.SootUtils.getConstructor
import org.kotsuite.soot.SootUtils.getVisibility
import org.kotsuite.soot.TestDoubleType
import org.kotsuite.soot.SootUtils.getObjectName
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
     * TODO: Code reuse
     *
     * @param targetMethod 待测函数
     * @param testCaseName 测试用例名
     * @return
     */
    private fun generateTestCaseForMethod(targetMethod: SootMethod, testCaseName: String): TestCase {
        val testCase = TestCase(testCaseName, targetMethod)
        val targetClass = targetMethod.declaringClass
        val targetObjectName = targetClass.getObjectName()
        val targetObject = Variable(targetObjectName, targetClass.type)

        // 初始化实例对象
        when (targetClass.getInitializationType()) {
            // 使用构造函数初始化
            InitializationType.CONSTRUCTOR -> {
                initializeTargetObjectByConstructor(testCase, targetObject, targetClass)
            }
            // 使用 mock 或 spy 初始化对象，并随机 mock 对象的行为
            InitializationType.TEST_DOUBLE -> {
                val testDoubleType = targetClass.getTestDoubleType(true)
                initializeTargetObjectByTestDouble(testCase, targetObject, targetClass, testDoubleType)

                // 模拟 spyk 对象的行为
                val methodsToMock = getMockWhenMethods(targetClass, targetMethod)
                methodsToMock.forEach {
                    mockBehavior(testCase, targetObject, it)
                }
            }
        }

        val methodCallParameters = createMethodParameters(testCase, targetMethod)
        val methodCallAction = generateMethodCallAction(targetObject, targetMethod, methodCallParameters)
        testCase.actions.add(methodCallAction)

        return testCase
    }

    /**
     * Initialize target object by constructor
     *
     * Steps:
     * 1. Generate constructor parameters
     * 2. Generate constructor action
     *
     * @param testCase
     * @param targetObject
     * @param targetClass
     */
    private fun initializeTargetObjectByConstructor(
        testCase: TestCase,
        targetObject: Variable,
        targetClass: SootClass,
    ) {
        // Get the constructor method of the target class
        val constructor = targetClass.getConstructor()
        if (constructor == null) {
            val errorMsg = "There is no constructor for class ${targetClass.name}"
            log.error(errorMsg)
            throw Exception(errorMsg)
        }

        // Create parameters of the constructor method
        val constructorParameters = createMethodParameters(testCase, constructor)

        // Generate constructor action
        val constructorAction = generateConstructorAction(targetObject, targetClass, constructor, constructorParameters)
        testCase.actions.add(constructorAction)
    }

    /**
     * Initialize target object by test double
     *
     * Types:
     * 1. Generate mockk object
     *      1. Generate mockk action
     * 2. Generate spyk object
     *      1. Generate object to spyk
     *      2. Generate spyk action
     *
     * @param testCase
     * @param targetObject
     * @param targetClass
     */
    private fun initializeTargetObjectByTestDouble(
        testCase: TestCase,
        targetObject: Variable,
        targetClass: SootClass,
        testDoubleType: TestDoubleType,
    ) {
        when(testDoubleType) {
            TestDoubleType.JMOCKK_MOCK -> {
                val testDoubleAction = generateJMockkTestDoubleAction(targetObject, targetClass)
                testCase.actions.add(testDoubleAction)
            }
            TestDoubleType.JMOCKK_SPY -> {
                val objectToSpyk = Variable(targetClass.getObjectName(), targetClass.type)
                initializeTargetObjectByConstructor(testCase, objectToSpyk, targetClass)

                val testDoubleAction = generateJSpykTestDoubleAction(targetObject, objectToSpyk, targetClass)
                testCase.actions.add(testDoubleAction)
            }
            else -> {
                TODO("这里需要实现Mockito的mock和spy,但暂时用不到")
            }
        }
    }

    /**
     * Mock behavior to the method of the test double
     *
     * *Note: Because we only mock the method which has no parameters and has a return value,
     * so we just pass an empty parameter list to the [MockWhenAction]*
     *
     * @param testCase the test case
     * @param targetObject the test double
     * @param methodToMock the method needs to be mocked
     */
    private fun mockBehavior(
        testCase: TestCase,
        targetObject: Variable,
        methodToMock: SootMethod,
    ) {
        val parameters = listOf<Parameter>()
        val returnParameter = createMethodReturnParameter(testCase, methodToMock)
        val mockWhenAction = generateMockWhenAction(targetObject, methodToMock, parameters, returnParameter)
        testCase.actions.add(mockWhenAction)
    }

    private fun generateConstructorAction(
        targetObject: Variable,
        targetClass: SootClass,
        targetConstructor: SootMethod,
        constructorParameters: List<Parameter>
    ): ConstructorAction {
        return ConstructorAction(targetObject, targetClass, targetConstructor, constructorParameters)
    }

    /**
     * Generate JSpyk test double action
     *
     * @param targetObject the object to spy
     * @param objectToSpyk the result spy object
     * @param targetClass the target class to spy
     * @return [JSpykTestDoubleAction]
     */
    private fun generateJSpykTestDoubleAction(
        targetObject: Variable, objectToSpyk: Variable, targetClass: SootClass
    ): JSpykTestDoubleAction {
        return JSpykTestDoubleAction(targetObject, objectToSpyk, targetClass)
    }

    /**
     * Generate JMockk test double action
     *
     * @param targetObject the result mock object
     * @param targetClass the target class to mock
     * @return [JMockkTestDoubleAction]
     */
    private fun generateJMockkTestDoubleAction(
        targetObject: Variable, targetClass: SootClass
    ): JMockkTestDoubleAction {
        return JMockkTestDoubleAction(targetObject, targetClass, true)
    }

    /**
     * Generate mock when action
     *
     * @param objectToMock object to mock
     * @param methodToMock the method needs to be mocked
     * @param parameter the parameter of the method call
     * @param returnParameter the return value of the method call
     * @return [MockWhenAction]
     */
    private fun generateMockWhenAction(
        objectToMock: Variable,
        methodToMock: SootMethod,
        parameter: List<Parameter>,
        returnParameter: Parameter,
    ): MockWhenAction {
        return MockWhenAction(
            MockWhenActionType.JMOCKK,
            objectToMock,
            methodToMock.getVisibility(),
            methodToMock,
            parameter,
            returnParameter,
        )
    }

    private fun generateMethodCallAction(
        variable: Variable,
        targetMethod: SootMethod,
        methodCallParameters: List<Parameter>,
    ): MethodCallAction {
        return MethodCallAction(variable, targetMethod, methodCallParameters)
    }

    /**
     * Create method parameters and returns it
     *
     * @param testCase the test case which contains the method call
     * @param targetMethod the target method
     * @return the parameters of the method call
     */
    private fun createMethodParameters(
        testCase: TestCase,
        targetMethod: SootMethod,
    ): List<Parameter> {
        return targetMethod.parameterTypes.map { createParameter(testCase, it) }
    }

    /**
     * Create method return parameter
     *
     * @param testCase the test case which contains the method call
     * @param targetMethod the target method
     * @return the return parameter of the method call
     */
    private fun createMethodReturnParameter(testCase: TestCase, targetMethod: SootMethod): Parameter {
        return createParameter(testCase, targetMethod.returnType)
    }

    /**
     * Create parameter for a type
     *
     * @param testCase the test case which contains the method cal
     * @param type the parameter type
     * @return the [Parameter] with random value
     */
    private fun createParameter(testCase: TestCase, type: Type): Parameter {
        return when(type) {
            is PrimType -> {
                val value = ValueGenerator.generatePrimValue(type)
                testCase.values.add(value)
                PrimParameter(type, testCase.values.size - 1)
            }
            is RefType -> {
                if (type == RefType.v(CommonClassConstants.string_class_name)) {
                    val value = ValueGenerator.generateStringValue()
                    testCase.values.add(value)
                    StringParameter(testCase.values.size - 1)
                } else {
                    val parameterVariable = Variable(type.sootClass.getObjectName(), type)
                    initializeTargetObjectByTestDouble(testCase, parameterVariable, type.sootClass, TestDoubleType.JMOCKK_MOCK)
                    RefTypeParameter(parameterVariable)
                }
            }
            is ArrayType -> {
                val value = ValueGenerator.generateArrayValue(type)
                testCase.values.add(value)
                ArrayParameter(type, testCase.values.size - 1)
            }
            else -> {
                val errorMsg = "Unsupported parameter type: $type"
                log.error(errorMsg)
                throw Exception(errorMsg)
            }
        }
    }

    /**
     * Get initialization type, constructor or test double?
     */
    private fun SootClass.getInitializationType(): InitializationType {
        return InitializationType.TEST_DOUBLE
    }

    /**
     * Get test double type
     *
     * @return
     */
    private fun SootClass.getTestDoubleType(isTargetClass: Boolean): TestDoubleType {
        return if (isTargetClass) {
            TestDoubleType.JMOCKK_SPY
        } else {
            TestDoubleType.JMOCKK_MOCK
        }
    }

    /**
     * Get mock when methods
     *
     * @param targetClass the class needs to mock behaviors
     * @param targetMethod the target method of the test case
     * @return the behaviors need to be mocked
     */
    private fun getMockWhenMethods(targetClass: SootClass, targetMethod: SootMethod): List<SootMethod> {
        return targetClass.methods.filter {
            it.parameterCount == 0 && it.returnType != VoidType.v() && it.name != targetMethod.name
        }
    }

    @Override
    override fun toString() = "Random Strategy"
}
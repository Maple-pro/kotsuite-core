package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.Population
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.jimple.TestClassJimpleGenerator.generateJimpleTestClass
import org.kotsuite.ga.solution.WholeSolution
import soot.*

object JimpleGenerator {
    private val log = LogManager.getLogger()

    /**
     * 根据 [WholeSolution.classSolutions] 生成 SootClass 列表
     *
     * @param [generateAssert] 是否需要生成断言
     * @return [WholeSolution] 中的 SootClass 列表
     */
    fun WholeSolution.generateJimpleTestClasses(generateAssert: Boolean): List<SootClass> {
        val testClasses = this.classSolutions.map { it.testClass }
        return testClasses.mapNotNull {
            try {
                it.generateJimpleTestClass(generateAssert = generateAssert)
            } catch (e: Exception) {
                log.error("Failed to generate jimple class for the test class: ${it.testClassName}")
                log.error(e.message)
                log.error(e.stackTraceToString())
                null
            }
        }
    }

    /**
     * Returns a soot class generated from the given test cases.
     *
     * @return the generated SootClasses
     */
    fun Population.generateJimpleTestClass(): SootClass {
        // generate a dummy test class, e.g., `TempExampleFooRound1ID0`
        val targetClass = this.targetMethod.declaringClass
        val dummyTestClassName = this.generatePopulationClassName()

        val dummyTestClass = TestClass(dummyTestClassName, targetClass.packageName, this.round)
        dummyTestClass.testCases = this.testCases.toMutableList()

        return dummyTestClass.generateJimpleTestClass()
    }

}
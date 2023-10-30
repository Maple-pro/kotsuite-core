package org.kotsuite.ga.jimple

import org.kotsuite.ga.chromosome.Population
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.jimple.TestClassJimpleGenerator.generateJimpleTestClass
import org.kotsuite.ga.solution.WholeSolution
import soot.*

object JimpleGenerator {

    fun WholeSolution.generateJimpleTestClasses(generateAssert: Boolean): List<SootClass> {
        val testClasses = this.classSolutions.map { it.testClass }
        return testClasses.map {
            it.generateJimpleTestClass(generateAssert = generateAssert)
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
        val dummyTestClassName = this.getPopulationClassName()

        val dummyTestClass = TestClass(dummyTestClassName, targetClass.packageName, this.round)
        dummyTestClass.testCases = this.testCases.toMutableList()

        return dummyTestClass.generateJimpleTestClass()
    }

}
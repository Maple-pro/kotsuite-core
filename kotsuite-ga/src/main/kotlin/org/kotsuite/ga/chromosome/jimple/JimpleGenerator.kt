package org.kotsuite.ga.chromosome.jimple

import org.kotsuite.ga.chromosome.Population
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.solution.WholeSolution
import soot.*

object JimpleGenerator {

    fun generateTestClassesFromWholeSolution(wholeSolution: WholeSolution): List<SootClass> {
        val testClasses = wholeSolution.classSolutions.map { it.testClass }
        return testClasses.map { TestClassJimpleGenerator.generate(it) }
    }

    /**
     * Returns a soot class generated from the given test cases.
     *
     * @param   population  list of TestCase
     * @return              the generated SootClasses
     */
    fun generateTestClassFromPopulation(population: Population): SootClass {
        // generate a dummy test class, e.g., `TempExampleFooRound1`
        val targetClass = population.targetMethod.declaringClass
        val targetClassName = targetClass.shortName
        val capitalizedMethodName = population.targetMethod.name.replaceFirstChar { it.uppercase() }
        val dummyTestClassName = "Temp$targetClassName${capitalizedMethodName}Round${population.round}"

        val dummyTestClass = TestClass(dummyTestClassName, targetClass.packageName, population.round)
        dummyTestClass.testCases = population.testCases.toMutableList()

        return TestClassJimpleGenerator.generate(dummyTestClass)
    }

}
package org.kotsuite.ga.jimple

import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.chromosome.generator.TestClassGenerator
import soot.*

object JimpleGenerator {

    fun generateTestClasses(testClasses: List<TestClass>): List<SootClass> {
        val jimpleClasses = testClasses.map { TestClassGenerator.generate(it) }
        
//        val targetMethods = jimpleClasses.map { it.methods }.reduce { methods, sootMethods -> methods + sootMethods }

        val targetMethods = jimpleClasses[0].methods.filter { !it.name.equals("<init>") }

        // Generate main class
//        val mainClass = Utils.generateMainClass(Configs.mainClass, targetMethods)

        // Transform testClasses to jimple classes
//        return jimpleClasses + listOf(mainClass)
        return jimpleClasses
    }

    /**
     * Returns a soot class generated from the given test cases.
     *
     * @param   testCases   a list of TestCase
     * @param   testClass   the TestClass which the testCases belong to
     * @return              the generated SootClass
     */
    fun generateFromCurPopulation(testCases: List<TestCase>, testClass: TestClass): SootClass {
        // Used in ga strategy: temporarily generate a junit class which contains the population (current round of test cases)
        // Create a temp class `ExampleTestTestPrintRound1` --> ExampleTest.TestPrint() Round1

        TODO()

    }

}
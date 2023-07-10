package org.kotsuite.ga.coverage.fitness

import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.jimple.JimpleGenerator

object TestSuiteFitness {

    fun generateTestSuiteFitness(testCases: List<TestCase>, testClass: TestClass): Fitness {

        // 1. Use soot to generate main class and main method, and the main method body is empty
        val mainClassName = "SootMain"
        val jimpleClass = JimpleGenerator.generateFromCurPopulation(testCases, testClass)

        // 2. Print main class and test cases into file using jasmin format

        // 3. Modify main method body using java agent to get coverage information individually

        // 3. Run application with the main class and jacoco agent, which will generate the .exec file

        // 4. Analyze the .exec file to generate the coverage information

        TODO()
    }

}
package org.kotsuite.ga.coverage.fitness

import org.kotsuite.ga.chromosome.TestCase
import soot.SootMethod

object TestCaseFitness {

    fun generateTestCaseFitness(
        testCase: TestCase,
        targetMethod: SootMethod,
        populationOutputPath: String,
        classesFilePath: String
    ) {
        // 1. Use soot to generate main class and main method, and the main method body is generated with testCase

        // 2. Print main class into file using jasmin format

        // 3. Run application with the main class and jacoco agent, which will generate the .exec file

        // 4. Analyze the .exec file to generate the coverage information

        TODO("Not yet implement.")
    }

}
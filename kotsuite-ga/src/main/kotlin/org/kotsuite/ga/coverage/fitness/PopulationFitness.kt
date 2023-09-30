package org.kotsuite.ga.coverage.fitness

import org.kotsuite.Configs
import org.kotsuite.ga.chromosome.Population
import org.kotsuite.ga.jimple.JimpleGenerator
import org.kotsuite.ga.printer.JasminPrinter
import org.kotsuite.ga.coverage.ExecResolver
import org.kotsuite.ga.coverage.JacocoUtils
import org.kotsuite.utils.SootUtils
import soot.SootClass
import soot.SootMethod

object PopulationFitness {

    fun generatePopulationFitness(population: Population): Fitness? {

        // Generate jimple test class
        val jimpleTestClass = JimpleGenerator.generateTestClassFromPopulation(population)

        // Create a main class `SootMain` with an empty main method
        val jimpleMainClass = SootUtils.generateMainClass(Configs.mainClass, listOf())

        // Print main class and test classes into file using jasmin format
        JasminPrinter.printJasminFile(jimpleTestClass)
        JasminPrinter.printJasminFile(jimpleMainClass)

        // Generate fitness for each test cases
        population.testCases.forEach { testCase ->
            val assertFilePath = "" // TODO

            // Run application with jacoco agent and kotsuite agent, which will generate the .exec file
            // Analyze the .exec file to generate the coverage information
            TestCaseFitness(
                jimpleTestClass, testCase, population.targetMethod, jimpleMainClass, assertFilePath
            ).generateTestCaseFitness()

            // TODO: pass a assert file path, and use the assert file path to generate the assert for the test case
            testCase.generateAssertByFile(assertFilePath)
        }

        // Generate fitness value for whole population
        val execDataFile = Configs.getExecFilePath(jimpleTestClass.name, "*")
        generateTotalPopulationExec(execDataFile, jimpleTestClass, jimpleMainClass)
        population.fitness = generateTotalPopulationFitness(execDataFile, population.targetMethod)

        return population.fitness
    }

    private fun generateTotalPopulationExec(
        execDataFile: String, jimpleTestClass: SootClass, jimpleMainClass: SootClass
    ) {
        JacocoUtils.generatePopulationExecFile(
            jimpleMainClass.name,
            jimpleTestClass.name,
            execDataFile,
            Configs.sootOutputPath,
        )
    }

    private fun generateTotalPopulationFitness(execDataFile: String, targetMethod: SootMethod): Fitness {
        val execResolver = ExecResolver(
            "MyApplication",
            execDataFile,
            Configs.sootOutputPath,
            Configs.sourceCodePath,
        )

        return execResolver.getTargetMethodFitness(targetMethod)
    }
}
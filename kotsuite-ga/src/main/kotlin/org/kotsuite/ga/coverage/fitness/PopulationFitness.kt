package org.kotsuite.ga.coverage.fitness

import org.kotsuite.Configs
import org.kotsuite.ga.chromosome.Population
import org.kotsuite.ga.printer.JasminPrinter
import org.kotsuite.ga.coverage.ExecResolver
import org.kotsuite.ga.coverage.JacocoUtils
import org.kotsuite.ga.jimple.JimpleGenerator.generateJimpleTestClass
import soot.SootClass
import soot.SootMethod

object PopulationFitness {

    fun generatePopulationFitness(population: Population, assertFilePath: String) {
        // Generate jimple test class
        val jimpleTestClass = population.generateJimpleTestClass()

        // Print main class and test classes into file using jasmin format
        JasminPrinter.printJasminFile(jimpleTestClass)

        // Generate fitness for each test cases
        population.testCases.forEach { testCase ->
            // Run application with jacoco agent and kotsuite agent, which will generate the .exec file
            // Analyze the .exec file to generate the coverage information
            TestCaseFitness(
                jimpleTestClass,
                testCase,
                population.targetMethod,
                assertFilePath
            ).generateTestCaseFitness()
        }

        // Generate fitness value for whole population
        val execDataFile = Configs.getExecFilePath(jimpleTestClass.name, "*")
        generateTotalPopulationExec(
            execDataFile,
            jimpleTestClass,
        )
        population.fitness = generateTotalPopulationFitness(execDataFile, population.targetMethod)
    }

    private fun generateTotalPopulationExec(
        execDataFile: String,
        jimpleTestClass: SootClass,
    ) {
        JacocoUtils.generatePopulationExecFile(
            Configs.mainClass,
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
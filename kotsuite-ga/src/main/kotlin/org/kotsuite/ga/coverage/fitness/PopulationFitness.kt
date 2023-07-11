package org.kotsuite.ga.coverage.fitness

import org.kotsuite.ga.Configs
import org.kotsuite.ga.chromosome.Population
import org.kotsuite.ga.chromosome.jimple.JimpleGenerator
import org.kotsuite.ga.chromosome.printer.JasminPrinter
import org.kotsuite.ga.utils.SootUtils

object PopulationFitness {

    fun generatePopulationFitness(population: Population): Fitness {

        // Generate jimple test classes
        val jimpleTestClass = JimpleGenerator.generateTestClassFromPopulation(population)

        // Create a main class `SootMain` with an empty main method
        val jimpleMainClass = SootUtils.generateMainClass(Configs.mainClass, listOf())

        // Print main class and test classes into file using jasmin format
        JasminPrinter.printJasminFile(jimpleTestClass)
        JasminPrinter.printJasminFile(jimpleMainClass)

        population.testCases.forEach {
            TODO()
            // Run application with jacoco agent and kotsuite agent, which will generate the .exec file

            // Analyze the .exec file to generate the coverage information

        }

        return Fitness(0.0, 0.0)

    }

}
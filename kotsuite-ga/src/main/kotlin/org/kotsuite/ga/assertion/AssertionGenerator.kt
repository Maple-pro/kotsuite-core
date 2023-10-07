package org.kotsuite.ga.assertion

import org.kotsuite.ga.chromosome.Population
import java.io.File

object AssertionGenerator {
    fun addAssertions(population: Population, assertFile: File) {
        population.addAssertions(assertFile)
    }
}
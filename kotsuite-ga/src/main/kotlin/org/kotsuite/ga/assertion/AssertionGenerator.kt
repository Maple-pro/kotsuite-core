package org.kotsuite.ga.assertion

import org.kotsuite.ga.chromosome.Population
import java.io.File

object AssertionGenerator {
    /**
     * 通过 [assertFile] 为 [population] 生成断言
     */
    fun addAssertions(population: Population, assertFile: File) {
        population.appendAssertionsToFile(assertFile)
    }
}
package org.kotsuite.ga.chromosome

import org.kotsuite.ga.coverage.fitness.Fitness
import soot.SootMethod

class Population(
    val targetMethod: SootMethod,
    var round: Int,
    val testCases: List<TestCase>,
) {

    var fitness: Fitness? = null

    /**
     * TODO
     * Select test cases from old population to form a new population
     *
     * @return
     */
    fun select(): Population {
        return this
    }

    /**
     * TODO
     * Mutate
     *
     * @return
     */
    fun mutate(): Population {
        return this
    }

    /**
     * TODO
     * Crossover
     *
     * @return
     */
    fun crossover(): Population {
        return this

    }

}
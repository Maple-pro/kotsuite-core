package org.kotsuite.ga.chromosome

import soot.SootMethod

class Population(
    val targetMethod: SootMethod,
    val round: Int,
    val testCases: List<TestCase>,
) {

    /**
     * Select test cases from old population to form a new population
     *
     * @return
     */
    fun select(): Population {
        TODO()

    }

    fun mutate(): Population {
        TODO()
    }

    fun crossover(): Population {
        TODO()

    }

}
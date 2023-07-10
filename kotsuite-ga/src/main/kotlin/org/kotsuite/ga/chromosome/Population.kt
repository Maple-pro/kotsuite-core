package org.kotsuite.ga.chromosome

import soot.SootMethod

class Population(
    val targetMethod: SootMethod,
    val testClass: TestClass,
    val round: Int,
    val testCases: List<TestCase>,
) {

    fun mutate() {

    }

    fun crossover() {

    }

}
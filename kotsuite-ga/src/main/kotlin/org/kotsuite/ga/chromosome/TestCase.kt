package org.kotsuite.ga.chromosome

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.action.Action
import org.kotsuite.ga.chromosome.value.Value
import org.kotsuite.ga.coverage.fitness.Fitness
import soot.SootMethod

class TestCase(
    val testCaseName: String,
    val targetMethod: SootMethod,
    val round: Int
): Element() {
    private val log = LogManager.getLogger()

    val actions = mutableListOf<Action>()
    val values = mutableListOf<Value>()
    var assertion: Assertion? = null
    var fitness: Fitness? = null
}
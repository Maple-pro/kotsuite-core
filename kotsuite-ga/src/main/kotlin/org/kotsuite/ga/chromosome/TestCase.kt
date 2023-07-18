package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.action.Action
import org.kotsuite.ga.chromosome.value.Value
import org.kotsuite.ga.coverage.fitness.Fitness
import soot.SootMethod

class TestCase(
    val testCaseName: String,
    val targetMethod: SootMethod,
    val round: Int
): Element() {
    val actions = mutableListOf<Action>()
    val values = mutableListOf<Value>()
    var assertValue: Any? = null  // assert value
    var fitness: Fitness? = null
}
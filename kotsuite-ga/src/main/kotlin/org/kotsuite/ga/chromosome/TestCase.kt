package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.action.Action
import org.kotsuite.ga.chromosome.value.ChromosomeValue
import org.kotsuite.ga.coverage.fitness.Fitness
import soot.SootMethod

class TestCase(
    val testCaseName: String,
    val targetMethod: SootMethod,
): Element() {
    val actions = mutableListOf<Action>()
    val values = mutableListOf<ChromosomeValue>()
    var assertion: Assertion? = null
    var fitness: Fitness? = null
    var testResult = true
    var coverageHashCodes: List<Int> = emptyList() // 该测试用例的覆盖信息的 hash 值（针对 target class），用于判断是否与其他测试用例重复
}
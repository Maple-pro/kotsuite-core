package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.action.Action
import org.kotsuite.ga.chromosome.value.Value

class TestCase(val testCaseName: String): Element() {

    val actions = mutableListOf<Action>()
    val values = mutableListOf<Value>()
}
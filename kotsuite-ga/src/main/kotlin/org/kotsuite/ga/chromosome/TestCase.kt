package org.kotsuite.ga.chromosome

import soot.Value

class TestCase(val testCaseName: String): Element() {

    val actions = mutableListOf<Action>()
    val values = mutableListOf<Value>()
}
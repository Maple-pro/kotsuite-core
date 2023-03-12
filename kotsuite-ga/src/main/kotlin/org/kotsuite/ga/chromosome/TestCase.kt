package org.kotsuite.ga.chromosome

import soot.Value

class TestCase(val testCaseName: String): Element() {
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }

    val actions = mutableListOf<Action>()
    val values = mutableListOf<Value>()
}
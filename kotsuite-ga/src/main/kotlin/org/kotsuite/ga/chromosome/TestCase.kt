package org.kotsuite.ga.chromosome

class TestCase(val testCaseName: String): Element() {
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }

    val actions = mutableListOf<Action>()
    val values = mutableListOf<Value>()
}
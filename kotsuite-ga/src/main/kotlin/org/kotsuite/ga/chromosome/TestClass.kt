package org.kotsuite.ga.chromosome

class TestClass: Element() {
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }

    val testCases = mutableListOf<TestCase>()
}
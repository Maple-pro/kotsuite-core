package org.kotsuite.ga.chromosome

class TestClass(val testClassName: String): Element() {
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }

    val testCases = mutableListOf<TestCase>()
}
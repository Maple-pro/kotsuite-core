package org.kotsuite.ga.chromosome

class TestClass(val testClassName: String, var round: Int = 0): Element() {

    val testCases = mutableListOf<TestCase>()
    var packageName = ""
}
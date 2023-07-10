package org.kotsuite.ga.chromosome

class TestClass(
    val testClassName: String,
    var packageName: String = "",
    var round: Int = 0
): Element() {
    var testCases = mutableListOf<TestCase>()
}
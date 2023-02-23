package org.kotsuite.ga

import org.kotsuite.analysis.Analyzer
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.chromosome.TestClass
import soot.SootClass
import soot.SootMethod

abstract class GAStrategy {
    open fun generateTestSuite(): List<TestClass> {
        val testClasses = ArrayList<TestClass>()
        for (sootClass in Analyzer.classes) {
            testClasses += generateTestClassForClass(sootClass)
        }

        return testClasses
    }

    open fun generateTestClassForClass(sootClass: SootClass): TestClass {
        // Generate TestClass for target class
        val testClass = TestClass()

        // Generate TestCases for each method in the target class
        val testCases = sootClass.methods
            .filter { filterMethod(it) }
            .fold(listOf<TestCase>()) { sum, element -> sum + generateTestCasesForMethod(element)}

        // Add TestCases of each method in to TestClass
        testClass.testCases += testCases

        return testClass
    }

    abstract fun generateTestCasesForMethod(sootMethod: SootMethod): List<TestCase>

    open fun filterMethod(sootMethod: SootMethod): Boolean {
        return !(sootMethod.subSignature.equals("void <init>()") || sootMethod.name.equals("<init>"))
    }
}
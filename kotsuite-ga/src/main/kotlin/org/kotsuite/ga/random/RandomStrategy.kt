package org.kotsuite.ga.random

import org.kotsuite.ga.GAStrategy
import org.kotsuite.ga.chromosome.TestCase
import soot.SootMethod

class RandomStrategy: GAStrategy() {

    override fun generateTestCasesForMethod(sootMethod: SootMethod): List<TestCase>{
        // TODO: Generate TestCases for method
        val testCases = ArrayList<TestCase>()
        println(sootMethod.signature)



        return testCases
    }

}
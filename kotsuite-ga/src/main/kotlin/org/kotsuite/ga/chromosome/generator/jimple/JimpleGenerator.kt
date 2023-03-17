package org.kotsuite.ga.chromosome.generator.jimple

import org.kotsuite.ga.chromosome.TestClass
import soot.SootClass

object JimpleGenerator {

    fun generateClasses(testClasses: List<TestClass>) = testClasses.map { TestClassGenerator.generate(it) }

}
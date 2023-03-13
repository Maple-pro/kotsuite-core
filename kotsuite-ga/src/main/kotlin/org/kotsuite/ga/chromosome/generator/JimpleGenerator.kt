package org.kotsuite.ga.chromosome.generator

import org.kotsuite.ga.chromosome.TestClass
import soot.SootClass

class JimpleGenerator {
    fun generateJimple(testClasses: List<TestClass>): List<SootClass> {
//        testClasses.forEach {
//            it.accept(JimpleGeneratorVisitor(jimpleFilesDir))
//        }
        return testClasses.map { JimpleGeneratorVisitor().visit(it) }
    }
}
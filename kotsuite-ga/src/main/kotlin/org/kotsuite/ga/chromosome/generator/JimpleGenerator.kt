package org.kotsuite.ga.chromosome.generator

import org.kotsuite.ga.chromosome.TestClass
import soot.SootClass

class JimpleGenerator(private val jimpleFilesDir: String) {
    fun generateJimple(testClasses: List<TestClass>): List<SootClass> {
//        testClasses.forEach {
//            it.accept(JimpleGeneratorVisitor(jimpleFilesDir))
//        }
        return testClasses.map { JimpleGeneratorVisitor(jimpleFilesDir).visit(it) }
    }
}
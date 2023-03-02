package org.kotsuite.ga.chromosome.generator

import org.kotsuite.ga.chromosome.TestClass

class JimpleGenerator(private val jimpleFilesDir: String) {
    fun generateJimple(testClasses: List<TestClass>) {
        testClasses.forEach {
            it.accept(JimpleGeneratorVisitor(jimpleFilesDir))
        }
    }
}
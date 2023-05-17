package org.kotsuite.ga.chromosome.generator.jimple

import org.kotsuite.ga.chromosome.TestClass
import soot.*
import soot.jimple.Jimple
import soot.jimple.NullConstant
import java.util.*

object JimpleGenerator {

    fun generateClasses(testClasses: List<TestClass>): List<SootClass> {
        val jimpleClasses = testClasses.map { TestClassGenerator.generate(it) }
        
//        val targetMethods = jimpleClasses.map { it.methods }.reduce { methods, sootMethods -> methods + sootMethods }

        val targetMethods = jimpleClasses[1].methods.filter { !it.name.equals("<init>") }

        // Generate main class
//        val mainClass = Utils.generateMainClass(mainClassName, targetMethods)

        // Transform testClasses to jimple classes
//        return jimpleClasses + listOf(mainClass)
        return jimpleClasses
    }

}
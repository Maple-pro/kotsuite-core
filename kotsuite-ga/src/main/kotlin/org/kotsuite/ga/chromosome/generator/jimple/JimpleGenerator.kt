package org.kotsuite.ga.chromosome.generator.jimple

import org.kotsuite.ga.chromosome.TestClass
import soot.Modifier
import soot.Scene
import soot.SootClass
import soot.SootMethod

object JimpleGenerator {

    fun generateClasses(testClasses: List<TestClass>): List<SootClass> {
        val mainClassName = "KotMain"
        val junitExecutorClassName = "KotJUnitExecutor"
        
        val jimpleClasses = testClasses.map { TestClassGenerator.generate(it) }
        
//        val targetMethods = jimpleClasses.map { it.methods }.reduce { methods, sootMethods -> methods + sootMethods }

        val targetMethods = jimpleClasses[1].methods.filter { !it.name.equals("<init>") }

        // Generate main class
        val mainClass = generateMainClass(mainClassName, targetMethods)
        
        // Generate JUnit executor class
//        val junitExecutorClass = generateJUnitExecutorClass(junitExecutorClassName, targetMethods)

        // Transform testClasses to jimple classes
        return jimpleClasses + listOf(mainClass)
    }
    
    private fun generateMainClass(mainClassName: String, targetMethods: List<SootMethod>): SootClass {
        Scene.v().loadClassAndSupport("java.lang.Object")
        
        val mainClass = SootClass(mainClassName, Modifier.PUBLIC)
        mainClass.superclass = Scene.v().getSootClass("java.lang.Object")
        Scene.v().addClass(mainClass)
        
        val mainMethod = TestClassGenerator.createMainMethod(targetMethods)
        mainClass.addMethod(mainMethod)

        return mainClass
    }
    
    private fun generateJUnitExecutorClass(junitExecutorClassName: String, targetMethods: List<SootMethod>): SootClass {
        TODO()
    }
    
    

}
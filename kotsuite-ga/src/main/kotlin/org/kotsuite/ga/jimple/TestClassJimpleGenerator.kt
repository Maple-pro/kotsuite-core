package org.kotsuite.ga.jimple

import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.jimple.TestCaseJimpleGenerator.generateJimpleTestMethod
import org.kotsuite.utils.soot.AnnotationUtils
import org.kotsuite.utils.soot.SootUtils.generateInitMethod
import soot.Modifier
import soot.Scene
import soot.SootClass

object TestClassJimpleGenerator {

    /**
     * Generate jimple class for the given TestClass
     *
     * @param printTestCaseName
     * @param generateAssert
     * @return
     */
    fun TestClass.generateJimpleTestClass(
        printTestCaseName: Boolean = false,
        generateAssert: Boolean = false,
    ): SootClass {
        // Resolve dependencies
        Scene.v().loadClassAndSupport("java.lang.Object")

        // Declare 'public class $element.testClassName'
        val sootClass = SootClass(this.getFullTestClassName(), Modifier.PUBLIC)

        // extends Object
        sootClass.superclass = Scene.v().getSootClass("java.lang.Object")

        // Create `@RunWith()` annotation
        sootClass.addTag(AnnotationUtils.generateRunWithMockitoAnnotation())

        Scene.v().addClass(sootClass)

        // Create <init> method
        val initMethod = sootClass.generateInitMethod()

        // Create methods
        val sootMethods = this.testCases.map {
            it.generateJimpleTestMethod(
                sootClass,
                printTestCaseName,
                generateAssert,
            )
        }

        // Add methods to class
        val methods = listOf(initMethod) + sootMethods
        methods.forEach { sootClass.addMethod(it) }

        return sootClass
    }

}
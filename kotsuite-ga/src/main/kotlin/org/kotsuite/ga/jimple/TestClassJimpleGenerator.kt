package org.kotsuite.ga.jimple

import org.kotsuite.CommonClassConstants
import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.jimple.TestCaseJimpleGenerator.generateJimpleTestMethod
import org.kotsuite.soot.extensions.generateInitMethod
import soot.Modifier
import soot.Scene
import soot.SootClass

object TestClassJimpleGenerator {

    /**
     * Generate jimple class for the given TestClass
     *
     * @param printTestCaseName whether to print the test case name in the test method
     * @param generateAssert whether to generate assert statement in the test method
     * @return the generated SootClass
     */
    fun TestClass.generateJimpleTestClass(
        printTestCaseName: Boolean = false,
        generateAssert: Boolean = false,
    ): SootClass {
        // Resolve dependencies
        Scene.v().loadClassAndSupport(CommonClassConstants.object_class_name)

        // Declare 'public class $element.testClassName'
        val sootClass = SootClass(this.getFullTestClassName(), Modifier.PUBLIC)

        // extends Object
        sootClass.superclass = Scene.v().getSootClass(CommonClassConstants.object_class_name)

        // Create `@RunWith()` annotation
//        sootClass.addTag(Annotation.generateRunWithMockitoAnnotation())

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
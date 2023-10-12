package org.kotsuite.ga.jimple

import org.kotsuite.ga.chromosome.*
import org.kotsuite.utils.SootUtils
import soot.Kind
import soot.Modifier
import soot.Scene
import soot.SootClass
import soot.tagkit.AnnotationClassElem
import soot.tagkit.AnnotationConstants
import soot.tagkit.AnnotationElem
import soot.tagkit.AnnotationTag
import soot.tagkit.VisibilityAnnotationTag

object TestClassJimpleGenerator {

    /**
     * TODO:
     * Add annotation: `RunWith(AndroidJUnit4:class)`, `RunWith(RobolectricTestRunner.class)`, `RunWith(MockitoJUnitRunner.class)`
     *
     * @param element
     * @param printTestCaseName
     * @param generateAssert
     * @return
     */
    fun generate(
        element: TestClass,
        printTestCaseName: Boolean = false,
        generateAssert: Boolean = false,
    ): SootClass {
        // Resolve dependencies
        Scene.v().loadClassAndSupport("java.lang.Object")

        // Declare 'public class $element.testClassName'
        val sootClass = SootClass(element.getFullTestClassName(), Modifier.PUBLIC)

        // extends Object
        sootClass.superclass = Scene.v().getSootClass("java.lang.Object")

        // Create `@RunWith()` annotation
        sootClass.addTag(SootUtils.generateRunWithMockitoAnnotation())

        Scene.v().addClass(sootClass)

        // Create <init> method
        val initMethod = SootUtils.createInitMethod(sootClass)

        // Create methods
        val sootMethods = element.testCases.map {
            TestCaseJimpleGenerator.generate(
                it, sootClass,
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
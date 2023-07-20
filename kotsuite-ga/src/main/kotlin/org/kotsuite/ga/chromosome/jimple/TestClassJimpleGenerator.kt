package org.kotsuite.ga.chromosome.jimple

import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.utils.SootUtils
import soot.Modifier
import soot.Scene
import soot.SootClass

object TestClassJimpleGenerator {

    /* TODO: to junit class, `RunWith(AndroidJUnit4:class)` */
    fun generate(
        element: TestClass,
        collectReturnValue: Boolean = false,
        printTestCaseName: Boolean = false,
        generateAssert: Boolean = false,
    ): SootClass {
        // Resolve dependencies
        Scene.v().loadClassAndSupport("java.lang.Object")

        // Declare 'public class $element.testClassName'
        val sootClass = SootClass("${element.packageName}.${element.testClassName}", Modifier.PUBLIC)

        // extends Object
        sootClass.superclass = Scene.v().getSootClass("java.lang.Object")
        Scene.v().addClass(sootClass)

        // Create <init> method
        val initMethod = SootUtils.createInitMethod(sootClass)

        // Create methods
        val sootMethods = element.testCases.map {
            TestCaseJimpleGenerator.generate(
                it, sootClass,
                collectReturnValue,
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
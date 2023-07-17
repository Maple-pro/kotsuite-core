package org.kotsuite.ga.chromosome.jimple

import org.kotsuite.ga.chromosome.TestCase
import soot.Modifier
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.VoidType
import soot.jimple.Jimple
import soot.jimple.StringConstant
import soot.jimple.internal.JimpleLocal
import soot.tagkit.AnnotationConstants
import soot.tagkit.AnnotationTag
import soot.tagkit.VisibilityAnnotationTag

object TestCaseJimpleGenerator {

    private val jimple = Jimple.v()

    fun generate(testcase: TestCase, sootClass: SootClass): SootMethod {
        val sootMethod = SootMethod(testcase.testCaseName, null, VoidType.v(), Modifier.PUBLIC)

        val defaultAnnotationTag = VisibilityAnnotationTag(AnnotationConstants.RUNTIME_INVISIBLE)
        val junitTestAnnotation = AnnotationTag("Lorg/junit/Test;")
        defaultAnnotationTag.addAnnotation(junitTestAnnotation)
        sootMethod.addTag(defaultAnnotationTag)

        // Create the method body
        val body = jimple.newBody(sootMethod)
        sootMethod.activeBody = body

        // Create this local
        val thisLocalsAndUnits = createThisLocal(sootClass)
        body.locals.addAll(thisLocalsAndUnits.locals)
        body.units.addAll(thisLocalsAndUnits.units)

        // Create `println` statement
        val printlnLocalsAndUnits = createPrintlnStmt(sootMethod.name)
        body.locals.addAll(printlnLocalsAndUnits.locals)
        body.units.addAll(printlnLocalsAndUnits.units)

        // Create statements
        testcase.actions.forEach {
            val localsAndUnits = ActionJimpleGenerator.generate(it, testcase.values, sootMethod)
            body.locals.addAll(localsAndUnits.locals)
            body.units.addAll(localsAndUnits.units)
        }

        // Create return statement
        val returnStmt = jimple.newReturnVoidStmt()
        body.units.add(returnStmt)

        return sootMethod
    }

    private fun createThisLocal(sootClass: SootClass): LocalsAndUnits {
        val thisLocal = JimpleLocal("this", sootClass.type)
        val thisStmt = jimple.newIdentityStmt(thisLocal, jimple.newThisRef(sootClass.type))

        return LocalsAndUnits(listOf(thisLocal), listOf(thisStmt))
    }

    private fun createPrintlnStmt(message: String): LocalsAndUnits {
        // Add local: message
        val messageLocal = jimple.newLocal("message", RefType.v("java.lang.String"))
        val messageAssignStmt = jimple.newAssignStmt(messageLocal, StringConstant.v(message))

        // Add local: java.io.printStream tmpRef
        val tmpRefLocal = jimple.newLocal("tmpRef", RefType.v("java.io.PrintStream"))

        // Add unit: `tmpRef = java.lang.System.out`,
        // note that System.out is an instance of java.io.printStream, and it is a static field of java.lang.System
        val tmpRefAssignStmt = jimple.newAssignStmt(
            tmpRefLocal,
            jimple.newStaticFieldRef(
                Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef()
            )
        )

        // Add unit: `tmpRef.println(message)`
        val printStreamClass = Scene.v().getSootClass("java.io.PrintStream")
        val printlnMethod = printStreamClass.getMethod("void println(java.lang.String)")
        val printlnInvokeStmt = jimple.newInvokeStmt(
            jimple.newVirtualInvokeExpr(tmpRefLocal, printlnMethod.makeRef(), messageLocal)
        )

        return LocalsAndUnits(
            listOf(messageLocal, tmpRefLocal),
            listOf(messageAssignStmt, tmpRefAssignStmt, printlnInvokeStmt)
        )
    }

}
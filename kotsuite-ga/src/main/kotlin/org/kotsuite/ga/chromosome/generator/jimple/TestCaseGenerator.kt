package org.kotsuite.ga.chromosome.generator.jimple

import org.kotsuite.ga.chromosome.TestCase
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.VoidType
import soot.jimple.Jimple
import soot.jimple.StringConstant
import soot.jimple.internal.JimpleLocal

object TestCaseGenerator {

    private val jimple = Jimple.v()

    fun generate(testcase: TestCase, sootClass: SootClass): SootMethod {
        val sootMethod = SootMethod(testcase.testCaseName, null, VoidType.v())

        // Create the method body
        val body = jimple.newBody(sootMethod)
        sootMethod.activeBody = body

        // Create this local
        val thisLocalsAndUnits = createThisLocal(sootClass)

        // Create `println` statement
        val printlnLocalsAndUnits = createPrintlnStmt(sootMethod.name)

        // Create statements
        val actionLocalsAndUnits = testcase.actions
            .map { ActionGenerator.generate(it) }
            .reduce { sum, element ->
                LocalsAndUnits(sum.locals + element.locals, sum.units + element.units)
            }

        // Create return statement
        val returnStmt = jimple.newReturnVoidStmt()

        // Add locals and units to body
        val locals = thisLocalsAndUnits.locals + printlnLocalsAndUnits.locals + actionLocalsAndUnits.locals
        val units = thisLocalsAndUnits.units + printlnLocalsAndUnits.units + actionLocalsAndUnits.units + returnStmt

        body.locals.addAll(locals)
        body.units.addAll(units)

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
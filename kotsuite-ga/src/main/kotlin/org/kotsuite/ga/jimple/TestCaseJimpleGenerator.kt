package org.kotsuite.ga.jimple

import org.kotsuite.CommonClassConstants
import org.kotsuite.PrintConstants
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.chromosome.action.MethodCallAction
import org.kotsuite.ga.jimple.ActionJimpleGenerator.generateJimpleStmt
import org.kotsuite.ga.jimple.PrimitiveAssertionJimpleGenerator.addAssertion
import org.kotsuite.soot.Annotation
import soot.*
import soot.Unit
import soot.jimple.Jimple
import soot.jimple.StringConstant
import soot.jimple.internal.JimpleLocal
import java.util.UUID

object TestCaseJimpleGenerator {

    private val jimple = Jimple.v()

    fun TestCase.generateJimpleTestMethod(
        sootClass: SootClass,
        printTestCaseName: Boolean = false,
        generateAssert: Boolean = false,
    ): SootMethod {
        val sootMethod = SootMethod(this.testCaseName, null, VoidType.v(), Modifier.PUBLIC)
        var returnValue: Local? = null

        // Create `@Test` annotation
        sootMethod.addTag(Annotation.generateTestAnnotation())

        // Create the method body
        val body = jimple.newBody(sootMethod)
        sootMethod.activeBody = body

        // Create this local
        createThisLocal(body, sootClass)

        // Add local: java.io.printStream tmpRef
        val printStreamRefLocal = jimple.newLocal("printStream", RefType.v(PrintConstants.printStream_class_name))

        // Add unit: `tmpRef = java.lang.System.out`,
        // note that System.out is an instance of java.io.printStream, and it is a static field of java.lang.System
        val printStreamRefAssignStmt = jimple.newAssignStmt(
            printStreamRefLocal,
            jimple.newStaticFieldRef(
                Scene.v().getField(PrintConstants.out_field_sig).makeRef()
            )
        )

        if (printTestCaseName) {
            body.locals.add(printStreamRefLocal)
            body.units.add(printStreamRefAssignStmt)
        }

        // Create `println` statement
        if (printTestCaseName) {
            createPrintStringStmt(body, sootMethod.name, printStreamRefLocal)
        }

        // Create statements
        this.actions.forEachIndexed { index, action ->
            if (index == this.actions.lastIndex
                && action is MethodCallAction
                && action.method.returnType !is VoidType
            ) {  // put the last action into print statement to collect return value
                // If the action is the last action, and it is a method call action, and it has return value,
                // then assign the method call expression to a variable
                returnValue = action.generateJimpleStmt(body, this.values, sootMethod, true)
            } else {  // just transform the action to normal invoke-statement
                action.generateJimpleStmt(body, this.values, sootMethod)
            }
        }

        // Create assert statement
        if (generateAssert) {
            this.addAssertion(body, returnValue)
        }

        // Create return statement
        val returnStmt = jimple.newReturnVoidStmt()
        body.units.add(returnStmt)

        return sootMethod
    }

    private fun createThisLocal(body: Body, sootClass: SootClass) {
        val thisLocal = JimpleLocal("this", sootClass.type)
        val thisStmt = jimple.newIdentityStmt(thisLocal, jimple.newThisRef(sootClass.type))

        body.locals.addAll(listOf(thisLocal))
        body.units.addAll(listOf(thisStmt))
    }

    private fun createPrintStringStmt(body: Body, message: String, printStreamRefLocal: Local) {
        // Add local: message
        val messageLocal = jimple.newLocal("message_${UUID.randomUUID()}", RefType.v(CommonClassConstants.string_class_name))
        val messageAssignStmt = jimple.newAssignStmt(messageLocal, StringConstant.v(message))

        body.locals.add(messageLocal)
        body.units.add(messageAssignStmt)

        createPrintValueStmt(body, messageLocal, printStreamRefLocal)
    }

    private fun createPrintValueStmt(body: Body, messageLocal: Local, printStreamRefLocal: Local) {
        val locals = mutableListOf<Local>()
        val units = mutableListOf<Unit>()

        if (messageLocal.type !is PrimType && messageLocal.type.toString() != CommonClassConstants.string_class_name) {
            return
        }

        // Add unit: `tmpRef.println(message)`
        val printlnMethod = Scene.v().getMethod(PrintConstants.getPrintlnSig(messageLocal.type))
        val printlnInvokeStmt = jimple.newInvokeStmt(
            jimple.newVirtualInvokeExpr(printStreamRefLocal, printlnMethod.makeRef(), messageLocal)
        )

        units.add(printlnInvokeStmt)

        body.locals.addAll(locals)
        body.units.addAll(units)
    }

}
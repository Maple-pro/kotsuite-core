package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.chromosome.action.MethodCallAction
import org.kotsuite.utils.SootUtils
import soot.*
import soot.Unit
import soot.jimple.Jimple
import soot.jimple.StringConstant
import soot.jimple.internal.JimpleLocal
import java.util.UUID

object TestCaseJimpleGenerator {

    private val log = LogManager.getLogger()

    private val jimple = Jimple.v()

    fun generate(
        testcase: TestCase, sootClass: SootClass,
        printTestCaseName: Boolean = false,
        generateAssert: Boolean = false,
    ): SootMethod {
        val sootMethod = SootMethod(testcase.testCaseName, null, VoidType.v(), Modifier.PUBLIC)
        var returnValue: Local? = null

        // Create `@Test` annotation
        sootMethod.addTag(SootUtils.generateTestAnnotation())

        // Create the method body
        val body = jimple.newBody(sootMethod)
        sootMethod.activeBody = body

        // Create this local
        createThisLocal(body, sootClass)

        // Add local: java.io.printStream tmpRef
        val printStreamRefLocal = jimple.newLocal("printStream}", RefType.v("java.io.PrintStream"))

        // Add unit: `tmpRef = java.lang.System.out`,
        // note that System.out is an instance of java.io.printStream, and it is a static field of java.lang.System
        val printStreamRefAssignStmt = jimple.newAssignStmt(
            printStreamRefLocal,
            jimple.newStaticFieldRef(
                Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef()
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
        testcase.actions.forEachIndexed { index, action ->
            if (index == testcase.actions.lastIndex
                && action is MethodCallAction
                && action.method.returnType !is VoidType
            ) {  // put the last action into print statement to collect return value
                // If the action is the last action, and it is a method call action, and it has return value,
                // then assign the method call expression to a variable
                returnValue = ActionJimpleGenerator.generate(body, action, testcase.values, sootMethod, true)
            } else {  // just transform the action to normal invoke-statement
                ActionJimpleGenerator.generate(body, action, testcase.values, sootMethod)
            }
        }

        // Create assert statement
        if (generateAssert) {
            PrimitiveAssertionJimpleGenerator.addAssertion(body, testcase, returnValue)
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
        val messageLocal = jimple.newLocal("message_${UUID.randomUUID()}", RefType.v("java.lang.String"))
        val messageAssignStmt = jimple.newAssignStmt(messageLocal, StringConstant.v(message))

        body.locals.add(messageLocal)
        body.units.add(messageAssignStmt)

        createPrintValueStmt(body, messageLocal, printStreamRefLocal)
    }

    private fun createPrintValueStmt(body: Body, messageLocal: Local, printStreamRefLocal: Local) {
        val locals = mutableListOf<Local>()
        val units = mutableListOf<Unit>()

        if (messageLocal.type !is PrimType && messageLocal.type.toString() != "java.lang.String") {
            return
        }

        // invoke `toString()` method
//        val objectClass = Scene.v().getSootClass("java.lang.Object")

//        if (messageLocal.type.toString() != "java.lang.String") {
//            val messageClass = Scene.v().getSootClass(messageLocal.type.toQuotedString())
//            val toStringMethodRef = messageClass.getMethod("java.lang.String toString()").makeRef()
//            val toStringInvokeExpr = jimple.newVirtualInvokeExpr(messageLocal, toStringMethodRef)
//
//            stringLocal = jimple.newLocal("messageString", RefType.v("java.lang.String"))
//            val toStringAssignStmt = jimple.newAssignStmt(stringLocal, toStringInvokeExpr)
//
//            locals.add(stringLocal)
//            units.add(toStringAssignStmt)
//        }

        // Add unit: `tmpRef.println(message)`
        val printStreamClass = Scene.v().getSootClass("java.io.PrintStream")
        val messageLocalTypeString = messageLocal.type.toString()
        val printlnMethod = printStreamClass.getMethod("void println($messageLocalTypeString)")
        val printlnInvokeStmt = jimple.newInvokeStmt(
            jimple.newVirtualInvokeExpr(printStreamRefLocal, printlnMethod.makeRef(), messageLocal)
        )

//        locals.add(printStreamRefLocal)
        units.add(printlnInvokeStmt)

        body.locals.addAll(locals)
        body.units.addAll(units)
    }

}
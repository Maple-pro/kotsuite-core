package org.kotsuite.ga.chromosome.jimple

import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.chromosome.action.MethodCallAction
import org.slf4j.LoggerFactory
import soot.*
import soot.Unit
import soot.dava.internal.javaRep.DIntConstant
import soot.jimple.DoubleConstant
import soot.jimple.FloatConstant
import soot.jimple.Jimple
import soot.jimple.LongConstant
import soot.jimple.StringConstant
import soot.jimple.internal.JimpleLocal
import soot.tagkit.AnnotationConstants
import soot.tagkit.AnnotationTag
import soot.tagkit.VisibilityAnnotationTag
import java.util.UUID

object TestCaseJimpleGenerator {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val jimple = Jimple.v()

    fun generate(
        testcase: TestCase, sootClass: SootClass,
        collectReturnValue: Boolean = false,
        printTestCaseName: Boolean = false,
        generateAssert: Boolean = false,
    ): SootMethod {
        val sootMethod = SootMethod(testcase.testCaseName, null, VoidType.v(), Modifier.PUBLIC)
        var returnValue: Local? = null
        val lastAction = testcase.actions.last() as MethodCallAction

        // Create `@Test` annotation
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

        if (collectReturnValue || printTestCaseName) {
            body.locals.add(printStreamRefLocal)
            body.units.add(printStreamRefAssignStmt)
        }

        if (printTestCaseName) {  // Create `println` statement
            val printlnLocalsAndUnits = createPrintStringStmt(sootMethod.name, printStreamRefLocal)
            body.locals.addAll(printlnLocalsAndUnits.locals)
            body.units.addAll(printlnLocalsAndUnits.units)
        }

        // Create statements
        testcase.actions.forEachIndexed { index, action ->
            if (index == testcase.actions.lastIndex
                && action is MethodCallAction
                && action.method.returnType !is VoidType
                ) {  // put the last action into print statement to collect return value

                // If the action is the last action, and it is a method call action, and it has return value,
                // then assign the method call expression to a variable
                val localsAndUnits = ActionJimpleGenerator.generate(action, testcase.values, sootMethod, true)
                body.locals.addAll(localsAndUnits.locals)
                body.units.addAll(localsAndUnits.units)

                returnValue = localsAndUnits.locals.first()

            } else {  // just transform the action to normal invoke-statement
                val localsAndUnits = ActionJimpleGenerator.generate(action, testcase.values, sootMethod)
                body.locals.addAll(localsAndUnits.locals)
                body.units.addAll(localsAndUnits.units)
            }
        }

        if (collectReturnValue && returnValue != null) {
            // print return value and return type
            // print "<assert>" prefix
            val printPrefixLocalsAndUnits = createPrintStringStmt("<assert>", printStreamRefLocal)
            body.locals.addAll(printPrefixLocalsAndUnits.locals)
            body.units.addAll(printPrefixLocalsAndUnits.units)

            // print return type, e.g., <type>int<type>
            val printReturnTypeLocalsAndUnits = createPrintStringStmt("<type>${lastAction.method.returnType}</type>", printStreamRefLocal)
            body.locals.addAll(printReturnTypeLocalsAndUnits.locals)
            body.units.addAll(printReturnTypeLocalsAndUnits.units)

            // print "<value>" prefix
            val printValuePrefixLocalsAndUnits = createPrintStringStmt("<value>", printStreamRefLocal)
            body.locals.addAll(printValuePrefixLocalsAndUnits.locals)
            body.units.addAll(printValuePrefixLocalsAndUnits.units)

            // print return value
            val printReturnLocalsAndUnits = createPrintValueStmt(returnValue!!, printStreamRefLocal)
            body.locals.addAll(printReturnLocalsAndUnits.locals)
            body.units.addAll(printReturnLocalsAndUnits.units)

            // print "</value>" suffix
            val printValueSuffixLocalsAndUnits = createPrintStringStmt("</value>", printStreamRefLocal)
            body.locals.addAll(printValueSuffixLocalsAndUnits.locals)
            body.units.addAll(printValueSuffixLocalsAndUnits.units)

            // print "</assert>" suffix
            val printSuffixLocalsAndUnits = createPrintStringStmt("</assert>", printStreamRefLocal)
            body.locals.addAll(printSuffixLocalsAndUnits.locals)
            body.units.addAll(printSuffixLocalsAndUnits.units)
        }

        if (generateAssert && testcase.assertType != null && testcase.assertValue != null && returnValue != null) {
            // generate assert statement
            val assertLocalsAndUnits = createAssertStatement(
                testcase.assertType!!, testcase.assertValue!!, returnValue!!
            )

            body.locals.addAll(assertLocalsAndUnits.locals)
            body.units.addAll(assertLocalsAndUnits.units)
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

    private fun createPrintStringStmt(message: String, printStreamRefLocal: Local): LocalsAndUnits {
        val locals = mutableListOf<Local>()
        val units = mutableListOf<Unit>()

        // Add local: message
        val messageLocal = jimple.newLocal("message_${UUID.randomUUID()}", RefType.v("java.lang.String"))
        val messageAssignStmt = jimple.newAssignStmt(messageLocal, StringConstant.v(message))

        locals.add(messageLocal)
        units.add(messageAssignStmt)

        val localsAndUnits = createPrintValueStmt(messageLocal, printStreamRefLocal)
        locals.addAll(localsAndUnits.locals)
        units.addAll(localsAndUnits.units)

        return LocalsAndUnits(locals, units)
    }

    private fun createPrintValueStmt(messageLocal: Local, printStreamRefLocal: Local): LocalsAndUnits {
        val locals = mutableListOf<Local>()
        val units = mutableListOf<Unit>()


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

        return LocalsAndUnits(locals, units)
    }

    private fun createAssertStatement(assertType: String, assertValue: String, actualValue: Local): LocalsAndUnits {
        val locals = mutableListOf<Local>()
        val units = mutableListOf<Unit>()

        // create the expected value
        val expectedValue = createLocalByTypeAndValue(assertType, assertValue) ?: return LocalsAndUnits(listOf(), listOf())
        val assertEqualsRef = getAssertEqualsMethodRef(assertType)
        val deltaDoubleConstant = DoubleConstant.v(0.1)
        val invokeStmt = jimple.newInvokeStmt(
            if (assertType != "double") jimple.newStaticInvokeExpr(assertEqualsRef, expectedValue, actualValue)
            else jimple.newStaticInvokeExpr(assertEqualsRef, expectedValue, actualValue, deltaDoubleConstant)
        )

//        locals.add(expectedValue)
        units.add(invokeStmt)

        return LocalsAndUnits(locals, units)
    }

    private fun createLocalByTypeAndValue(type: String, value: String): Value? {
        return when(type) {
            "boolean" -> {
                if (value == "true") DIntConstant.v(1, BooleanType.v())
                else DIntConstant.v(0, BooleanType.v())
            }
            "byte" -> null
            "char" -> null
            "double" -> DoubleConstant.v(value.toDouble())
            "float" -> FloatConstant.v(value.toFloat())
            "int" -> LongConstant.v(value.toLong())
            "long" -> LongConstant.v(value.toLong())
            "java.lang.String" -> StringConstant.v(value)
            else -> return null
        }
    }

    private fun getAssertEqualsMethodRef(type: String): SootMethodRef {
        val assertClass = Scene.v().getSootClass("org.junit.Assert")
        val subSig = when(type) {
            "int" -> "void assertEquals(long,long)"
            "double" -> "void assertEquals(double,double,double)"
            else -> "void assertEquals(java.lang.Object,java.lang.Object)"
        }
        return assertClass.getMethod(subSig).makeRef()
    }
}
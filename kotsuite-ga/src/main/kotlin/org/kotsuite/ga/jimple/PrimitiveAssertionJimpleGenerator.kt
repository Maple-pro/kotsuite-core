package org.kotsuite.ga.jimple

import org.kotsuite.ga.chromosome.TestCase
import soot.*
import soot.dava.internal.javaRep.DIntConstant
import soot.jimple.*

object PrimitiveAssertionJimpleGenerator {
    private val jimple = Jimple.v()
    private val assertClass = Scene.v().getSootClass("org.junit.Assert")
    private const val ASSERT_EQUALS_METHOD_SUB_REF = "void assertEquals(java.lang.Object,java.lang.Object)"
    private val assertEqualsMethodRef = assertClass.getMethod(ASSERT_EQUALS_METHOD_SUB_REF).makeRef()

    fun addAssertion(body: Body, testCase: TestCase, returnValue: Local?) {
        val assertion = testCase.assertion
        if (returnValue == null
            || assertion == null
            || assertion.assertType.isEmpty()
            || assertion.assertValue.isEmpty()
        ) {
            return
        }

        return createAssertStatement(body, assertion.assertType, assertion.assertValue, returnValue)
    }

    private fun createAssertStatement(body: Body, assertType: String, assertValue: String, actualValue: Local) {
        val assertTypeJavaClass = getJavaClass(assertType)
        when (assertTypeJavaClass) {
            "java.lang.Boolean" -> createBooleanAssertionStmt(body, assertValue, actualValue)
            "java.lang.Byte" -> createByteAssertionStmt(body, assertValue, actualValue)
            "java.lang.Character" -> createCharacterAssertionStmt(body, assertValue, actualValue)
            "java.lang.Double" -> createDoubleAssertionStmt(body, assertValue, actualValue)
            "java.lang.Float" -> createFloatAssertionStmt(body, assertValue, actualValue)
            "java.lang.Integer" -> createIntegerAssertionStmt(body, assertValue, actualValue)
            "java.lang.Long" -> createLongAssertionStmt(body, assertValue, actualValue)
            "java.lang.String" -> createStringAssertionStmt(body, assertValue, actualValue)
        }
    }

    private fun createBooleanAssertionStmt(body: Body, assertValue: String, actualValue: Local) {
        val expectedValue = if (assertValue == "true") {
            DIntConstant.v(1, BooleanType.v())
        } else {
            DIntConstant.v(0, BooleanType.v())
        }

        val expectedObject = jimple.newLocal("expectedObject", RefType.v("java.lang.Boolean"))
        val assignStmt = jimple.newAssignStmt(expectedObject, jimple.newNewExpr(RefType.v("java.lang.Boolean")))
        val constructorRef = Scene.v().getMethod("<java.lang.Boolean: void <init>(boolean)>").makeRef()
        val initStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(expectedObject, constructorRef, expectedValue))

        val invokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(assertEqualsMethodRef, expectedObject, actualValue)
        )

        body.locals.addAll(listOf(expectedObject))
        body.units.addAll(listOf(assignStmt, initStmt, invokeStmt))
    }

    private fun createByteAssertionStmt(body: Body, assertValue: String, actualValue: Local) {
        val expectedValue = DIntConstant.v(assertValue.toInt(), ByteType.v())

        val expectedObject = jimple.newLocal("expectedObject", RefType.v("java.lang.Byte"))
        val assignStmt = jimple.newAssignStmt(expectedObject, jimple.newNewExpr(RefType.v("java.lang.Byte")))
        val constructorRef = Scene.v().getMethod("<java.lang.Byte: void <init>(byte)>").makeRef()
        val initStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(expectedObject, constructorRef, expectedValue))

        val invokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(assertEqualsMethodRef, expectedObject, actualValue)
        )

        body.locals.addAll(listOf(expectedObject))
        body.units.addAll(listOf(assignStmt, initStmt, invokeStmt))
    }

    private fun createCharacterAssertionStmt(body: Body, assertValue: String, actualValue: Local) {
        val expectedValue = DIntConstant.v(assertValue.toInt(), CharType.v())

        val expectedObject = jimple.newLocal("expectedObject", RefType.v("java.lang.Character"))
        val assignStmt = jimple.newAssignStmt(expectedObject, jimple.newNewExpr(RefType.v("java.lang.Character")))
        val constructorRef = Scene.v().getMethod("<java.lang.Character: void <init>(char)>").makeRef()
        val initStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(expectedObject, constructorRef, expectedValue))

        val invokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(assertEqualsMethodRef, expectedObject, actualValue)
        )

        body.locals.addAll(listOf(expectedObject))
        body.units.addAll(listOf(assignStmt, initStmt, invokeStmt))
    }

    private fun createDoubleAssertionStmt(body: Body, assertValue: String, actualValue: Local) {
        val expectedValue = DoubleConstant.v(assertValue.toDouble())

        val expectedObject = jimple.newLocal("expectedObject", RefType.v("java.lang.Double"))
        val assignStmt = jimple.newAssignStmt(expectedObject, jimple.newNewExpr(RefType.v("java.lang.Double")))
        val constructorRef = Scene.v().getMethod("<java.lang.Double: void <init>(double)>").makeRef()
        val initStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(expectedObject, constructorRef, expectedValue))

        val invokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(assertEqualsMethodRef, expectedObject, actualValue)
        )

        body.locals.addAll(listOf(expectedObject))
        body.units.addAll(listOf(assignStmt, initStmt, invokeStmt))
    }

    private fun createFloatAssertionStmt(body: Body, assertValue: String, actualValue: Local) {
        val expectedValue = FloatConstant.v(assertValue.toFloat())

        val expectedObject = jimple.newLocal("expectedObject", RefType.v("java.lang.Float"))
        val assignStmt = jimple.newAssignStmt(expectedObject, jimple.newNewExpr(RefType.v("java.lang.Float")))
        val constructorRef = Scene.v().getMethod("<java.lang.Float: void <init>(float)>").makeRef()
        val initStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(expectedObject, constructorRef, expectedValue))

        val invokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(assertEqualsMethodRef, expectedObject, actualValue)
        )

        body.locals.addAll(listOf(expectedObject))
        body.units.addAll(listOf(assignStmt, initStmt, invokeStmt))
    }

    private fun createIntegerAssertionStmt(body: Body, assertValue: String, actualValue: Local) {
        val expectedValue = IntConstant.v(assertValue.toInt())

        val expectedObject = jimple.newLocal("expectedObject", RefType.v("java.lang.Integer"))
        val assignStmt = jimple.newAssignStmt(expectedObject, jimple.newNewExpr(RefType.v("java.lang.Integer")))
        val constructorRef = Scene.v().getMethod("<java.lang.Integer: void <init>(int)>").makeRef()
        val initStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(expectedObject, constructorRef, expectedValue))

        val invokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(assertEqualsMethodRef, expectedObject, actualValue)
        )

        body.locals.addAll(listOf(expectedObject))
        body.units.addAll(listOf(assignStmt, initStmt, invokeStmt))
    }

    private fun createLongAssertionStmt(body: Body, assertValue: String, actualValue: Local) {
        val expectedValue = LongConstant.v(assertValue.toLong())

        val expectedObject = jimple.newLocal("expectedObject", RefType.v("java.lang.Long"))
        val assignStmt = jimple.newAssignStmt(expectedObject, jimple.newNewExpr(RefType.v("java.lang.Long")))
        val constructorRef = Scene.v().getMethod("<java.lang.Long: void <init>(long)>").makeRef()
        val initStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(expectedObject, constructorRef, expectedValue))

        val invokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(assertEqualsMethodRef, expectedObject, actualValue)
        )

        body.locals.addAll(listOf(expectedObject))
        body.units.addAll(listOf(assignStmt, initStmt, invokeStmt))
    }

    private fun createStringAssertionStmt(body: Body, assertValue: String, actualValue: Local) {
        val expectedValue = StringConstant.v(assertValue)

        val expectedObject = jimple.newLocal("expectedObject", RefType.v("java.lang.String"))
        val assignStmt = jimple.newAssignStmt(expectedObject, jimple.newNewExpr(RefType.v("java.lang.String")))
        val constructorRef = Scene.v().getMethod("<java.lang.String: void <init>(string)>").makeRef()
        val initStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(expectedObject, constructorRef, expectedValue))

        val invokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(assertEqualsMethodRef, expectedObject, actualValue)
        )

        body.locals.addAll(listOf(expectedObject))
        body.units.addAll(listOf(assignStmt, initStmt, invokeStmt))
    }

    private fun getJavaClass(type: String): String {
        return type.removePrefix("class ")
    }
}
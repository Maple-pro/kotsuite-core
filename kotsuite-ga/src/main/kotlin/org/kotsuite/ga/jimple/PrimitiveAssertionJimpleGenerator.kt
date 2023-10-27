package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.AssertionConstants
import org.kotsuite.PrimitiveConstants
import org.kotsuite.ga.chromosome.TestCase
import soot.*
import soot.dava.internal.javaRep.DIntConstant
import soot.jimple.*

object PrimitiveAssertionJimpleGenerator {
    private val log = LogManager.getLogger()

    private val jimple = Jimple.v()
    private val assertEqualsMethodRef = Scene.v().getMethod(AssertionConstants.assertEquals_method_sig).makeRef()

    fun TestCase.addAssertion(body: Body, returnValue: Local?) {
        val assertion = this.assertion
        if (returnValue == null
            || assertion == null
            || assertion.assertType.isEmpty()
            || assertion.assertValue.isEmpty()
        ) {
            log.warn("Cannot add assertion statement")
            return
        }

        return createAssertStatement(body, assertion.assertType, assertion.assertValue, returnValue)
    }

    private fun createAssertStatement(body: Body, assertType: String, assertValue: String, actualValue: Local) {
        val assertTypeJavaClass = getJavaClass(assertType)
        val expectedValue = string2Value(assertValue, assertTypeJavaClass)

        val expectedObject = when (assertTypeJavaClass) {
            "java.lang.Boolean" -> {
                createExpectedObject(body, expectedValue, PrimitiveConstants.boolean_class_name, PrimitiveConstants.boolean_constructor_method_sig)
            }
            "java.lang.Byte" -> {
                createExpectedObject(body, expectedValue, PrimitiveConstants.byte_class_name, PrimitiveConstants.byte_constructor_method_sig)
            }
            "java.lang.Character" -> {
                createExpectedObject(body, expectedValue, PrimitiveConstants.character_class_name, PrimitiveConstants.character_constructor_method_sig)
            }
            "java.lang.Double" -> {
                createExpectedObject(body, expectedValue, PrimitiveConstants.double_class_name, PrimitiveConstants.double_constructor_method_sig)
            }
            "java.lang.Float" -> {
                createExpectedObject(body, expectedValue, PrimitiveConstants.float_class_name, PrimitiveConstants.float_constructor_method_sig)
            }
            "java.lang.Integer" -> {
                createExpectedObject(body, expectedValue, PrimitiveConstants.integer_class_name, PrimitiveConstants.integer_constructor_method_sig)
            }
            "java.lang.Long" -> {
                createExpectedObject(body, expectedValue, PrimitiveConstants.long_class_name, PrimitiveConstants.long_constructor_method_sig)
            }
            "java.lang.String" -> {
                createExpectedObject(body, expectedValue, PrimitiveConstants.string_class_name, PrimitiveConstants.string_constructor_method_sig)
            }
            else -> {
                throw Exception("Unsupported assert type: $assertTypeJavaClass")
            }
        }

        createAssertionStatement(body, expectedObject, actualValue)
    }

    private fun string2Value(assertValue: String, assertTypeJavaClass: String): Value {
        return when (assertTypeJavaClass) {
            "java.lang.Boolean" -> {
                if (assertValue == "true") {
                    DIntConstant.v(1, BooleanType.v())
                } else {
                    DIntConstant.v(0, BooleanType.v())
                }
            }
            "java.lang.Byte" -> DIntConstant.v(assertValue.toInt(), ByteType.v())
            "java.lang.Character" -> DIntConstant.v(assertValue.toInt(), CharType.v())
            "java.lang.Double" -> DoubleConstant.v(assertValue.toDouble())
            "java.lang.Float" -> FloatConstant.v(assertValue.toFloat())
            "java.lang.Integer" -> IntConstant.v(assertValue.toInt())
            "java.lang.Long" -> LongConstant.v(assertValue.toLong())
            "java.lang.String" -> StringConstant.v(assertValue)
            else -> {
                throw Exception("Unsupported assert type: $assertTypeJavaClass")
            }
        }
    }

    private fun createExpectedObject(
        body: Body,
        expectedValue: Value,
        expectedObjectClassName: String,
        expectedObjectClassConstructorSig: String
    ): Local {
        val expectedObject = jimple.newLocal("expectedObject", RefType.v(expectedObjectClassName))
        val assignStmt = jimple.newAssignStmt(expectedObject, jimple.newNewExpr(RefType.v(expectedObjectClassName)))
        val constructorRef = Scene.v().getMethod(expectedObjectClassConstructorSig).makeRef()
        val initStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(expectedObject, constructorRef, expectedValue))

        body.locals.addAll(listOf(expectedObject))
        body.units.addAll(listOf(assignStmt, initStmt))

        return expectedObject
    }

    private fun createAssertionStatement(body: Body, expectedObject: Local, actualValue: Local) {
        val invokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(assertEqualsMethodRef, expectedObject, actualValue)
        )

        body.units.addAll(listOf(invokeStmt))
    }

    private fun getJavaClass(type: String): String {
        return type.removePrefix("class ")
    }
}
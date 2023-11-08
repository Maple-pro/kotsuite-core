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
            return
        }

        return createAssertStatement(body, assertion.assertType, assertion.assertValue, returnValue)
    }

    private fun createAssertStatement(body: Body, assertType: String, assertValue: String, actualValue: Local) {
        val assertTypeJavaClass = getJavaClass(assertType)
        val expectedValue = string2Value(assertValue, assertTypeJavaClass)
        val className: String
        val constructorMethodSig: String

        when (assertTypeJavaClass) {
            "java.lang.Boolean" -> {
                className = PrimitiveConstants.boolean_class_name
                constructorMethodSig = PrimitiveConstants.boolean_constructor_method_sig
            }
            "java.lang.Byte" -> {
                className = PrimitiveConstants.byte_class_name
                constructorMethodSig = PrimitiveConstants.byte_constructor_method_sig
            }
            "java.lang.Character" -> {
                className = PrimitiveConstants.character_class_name
                constructorMethodSig = PrimitiveConstants.character_constructor_method_sig
            }
            "java.lang.Double" -> {
                className = PrimitiveConstants.double_class_name
                constructorMethodSig = PrimitiveConstants.double_constructor_method_sig
            }
            "java.lang.Float" -> {
                className = PrimitiveConstants.float_class_name
                constructorMethodSig = PrimitiveConstants.float_constructor_method_sig
            }
            "java.lang.Integer" -> {
                className = PrimitiveConstants.integer_class_name
                constructorMethodSig = PrimitiveConstants.integer_constructor_method_sig
            }
            "java.lang.Long" -> {
                className = PrimitiveConstants.long_class_name
                constructorMethodSig = PrimitiveConstants.long_constructor_method_sig
            }
            "java.lang.String" -> {
                className = PrimitiveConstants.string_class_name
                constructorMethodSig = PrimitiveConstants.string_constructor_method_sig
            }
            else -> {
                throw Exception("Unsupported assert type: $assertTypeJavaClass")
            }
        }

        val expectedObject = if (assertTypeJavaClass == "java.lang.String") {
            StringConstant.v(assertValue)
        } else {
            createExpectedObject(body, expectedValue, className, constructorMethodSig)
        }
        createAssertionStatement(body, expectedObject, actualValue)
    }

    /**
     * Transform `assertValue` (`String`) to `soot.Value`
     *
     * @param assertValue the expected value in assert statement (`string`)
     * @param assertTypeJavaClass the type of the expected value
     * @return the expected value in assert statement (`soot.Value`)
     */
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

    /**
     * Create expected object in assert statement
     *
     * @param body the method body which needs to generate expected object
     * @param expectedValue the expected value in assert statement (`soot.Value`)
     * @param expectedObjectClassName the class name of the expected object
     * @param expectedObjectClassConstructorSig
     * @return
     */
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

    private fun createAssertionStatement(body: Body, expectedObject: Value, actualValue: Local) {
        val invokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(assertEqualsMethodRef, expectedObject, actualValue)
        )

        body.units.addAll(listOf(invokeStmt))
    }

    private fun getJavaClass(type: String): String {
        return type.removePrefix("class ")
    }
}
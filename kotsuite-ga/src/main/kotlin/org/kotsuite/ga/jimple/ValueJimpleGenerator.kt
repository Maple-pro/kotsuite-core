package org.kotsuite.ga.jimple

import org.kotsuite.ga.chromosome.value.*
import soot.BooleanType
import soot.ByteType
import soot.CharType
import soot.Local
import soot.PrimType
import soot.RefType
import soot.ShortType
import soot.dava.internal.javaRep.DIntConstant
import soot.jimple.*

object ValueJimpleGenerator {

    fun generate(value: Value): soot.Value {
        return when (value) {
            is BooleanValue -> generateBooleanValue(value)
            is ByteValue -> generateByteValue(value)
            is CharValue -> generateCharValue(value)
            is DoubleValue -> generateDoubleValue(value)
            is FloatValue -> generateFloatValue(value)
            is IntValue -> generateIntValue(value)
            is LongValue -> generateLongValue(value)
            is ShortValue -> generateShortValue(value)
            is StringValue -> generateStringValue(value)
            is ArrayValue<*> -> generateArrayValue(value)
            else -> {
                throw Exception("Unsupported value type: $value")
            }
        }
    }

    private fun generateBooleanValue(value: BooleanValue): soot.Value {
        val tmp = if (value.booleanValue) 1 else 0
        return DIntConstant.v(tmp, BooleanType.v())
    }

    private fun generateByteValue(value: ByteValue): soot.Value {
        return DIntConstant.v(value.byteValue.toInt(), ByteType.v())
    }

    private fun generateCharValue(value: CharValue): soot.Value {
        return DIntConstant.v(value.charValue.code, CharType.v())
    }

    private fun generateDoubleValue(value: DoubleValue): soot.Value {
        return DoubleConstant.v(value.doubleValue)
    }

    private fun generateFloatValue(value: FloatValue): soot.Value {
        return FloatConstant.v(value.floatValue)
    }

    private fun generateIntValue(value: IntValue): soot.Value {
        return IntConstant.v(value.intValue)
    }

    private fun generateLongValue(value: LongValue): soot.Value {
        return LongConstant.v(value.longValue)
    }

    private fun generateShortValue(value: ShortValue): soot.Value {
        return DIntConstant.v(value.shortValue.toInt(), ShortType.v())
    }

    private fun generateStringValue(value: StringValue): soot.Value {
        return StringConstant.v(value.stringValue)
    }

    private fun<T> generateArrayValue(value: ArrayValue<T>): Local {
        val randomValue = (0..999999).random()
        val localName = "arr_$randomValue"

        val arrayLocal = Jimple.v().newLocal(localName, value.arrayType)
        val arraySize = IntConstant.v(value.arrayValue.size)
        val newArrayExpr = Jimple.v().newNewArrayExpr(value.arrayType.baseType, arraySize)
        val assignStmt = Jimple.v().newAssignStmt(arrayLocal, newArrayExpr)

        when(value.arrayType.baseType) {
            is PrimType -> {
            }
            RefType.v("java.lang.String") -> {

            }
            else -> {

            }
        }

        return arrayLocal
    }
}
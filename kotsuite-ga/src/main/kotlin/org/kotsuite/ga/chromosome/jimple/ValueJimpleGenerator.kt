package org.kotsuite.ga.chromosome.jimple

import org.kotsuite.ga.chromosome.value.*
import soot.BooleanType
import soot.dava.internal.javaRep.DIntConstant
import soot.jimple.DoubleConstant
import soot.jimple.IntConstant
import soot.jimple.StringConstant

object ValueJimpleGenerator {

    fun generate(value: Value): soot.Value {
        return when (value) {
            is IntValue -> generateIntValue(value)
            is DoubleValue -> generateDoubleValue(value)
            is BooleanValue -> generateBooleanValue(value)
            is StringValue -> generateStringValue(value)
            is ArrayValue<*> -> generateArrayValue(value)
            else -> {
                throw Exception("Unsupported value type: $value")
            }
        }
    }

    private fun generateIntValue(value: IntValue): soot.Value {
        return IntConstant.v(value.intValue)
    }

    private fun generateDoubleValue(value: DoubleValue): soot.Value {
        return DoubleConstant.v(value.doubleValue)
    }

    private fun generateBooleanValue(value: BooleanValue): soot.Value {
        val tmp = if (value.booleanValue) 1 else 0
        return DIntConstant.v(tmp, BooleanType.v())
    }

    private fun generateStringValue(value: StringValue): soot.Value {
        return StringConstant.v(value.stringValue)
    }

    private fun<T> generateArrayValue(value: ArrayValue<T>): soot.Value {
        TODO("Unsupported.")
    }
}
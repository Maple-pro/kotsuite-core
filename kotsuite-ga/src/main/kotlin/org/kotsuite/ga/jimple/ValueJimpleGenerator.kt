package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.value.*
import org.kotsuite.soot.Value
import org.kotsuite.soot.Value.generatePrimTypeJimpleValue
import soot.*
import soot.jimple.*

object ValueJimpleGenerator {
    private val log = LogManager.getLogger()

    fun generateJimpleValueFromChromosomeValue(body: Body, chromosomeValue: ChromosomeValue): soot.Value {
        return when (chromosomeValue) {
            is BooleanChromosomeValue -> Value.generateBooleanValue(chromosomeValue.booleanValue)
            is ByteChromosomeValue -> Value.generateByteValue(chromosomeValue.byteValue)
            is CharChromosomeValue -> Value.generateCharValue(chromosomeValue.charValue)
            is DoubleChromosomeValue -> Value.generateDoubleValue(chromosomeValue.doubleValue)
            is FloatChromosomeValue -> Value.generateFloatValue(chromosomeValue.floatValue)
            is IntChromosomeValue -> Value.generateIntValue(chromosomeValue.intValue)
            is LongChromosomeValue -> Value.generateLongValue(chromosomeValue.longValue)
            is ShortChromosomeValue -> Value.generateShortValue(chromosomeValue.shortValue)
            is StringChromosomeValue -> Value.generateStringValue(chromosomeValue.stringValue)
            is ArrayChromosomeValue<*> -> generateArrayValue(body, chromosomeValue)
            else -> {
                throw Exception("Unsupported value type: $chromosomeValue")
            }
        }
    }

    private fun <T : Any> generateArrayValue(body: Body, value: ArrayChromosomeValue<T>): Local {
        val randomValue = (0..999999).random()
        val localName = "arr_$randomValue"

        val arrayLocal = Jimple.v().newLocal(localName, value.arrayType)
        val arraySize = IntConstant.v(value.arrayValue.size)
        val newArrayExpr = Jimple.v().newNewArrayExpr(value.arrayType.baseType, arraySize)
        val assignStmt = Jimple.v().newAssignStmt(arrayLocal, newArrayExpr)

        body.locals.add(arrayLocal)
        body.units.add(assignStmt)

        for ((index, item) in value.arrayValue.withIndex()) {
            val arrayRef = Jimple.v().newArrayRef(arrayLocal, IntConstant.v(index))
            when (val baseType = value.arrayType.baseType) {
                is PrimType -> {
                    val itemValue = baseType.generatePrimTypeJimpleValue(item)
                    val itemAssignStmt = Jimple.v().newAssignStmt(arrayRef, itemValue)
                    body.units.add(itemAssignStmt)
                }

                is RefType -> {
                    if (value.arrayType.baseType == RefType.v("java.lang.String")) {
                        val itemValue = Value.generateStringValue(item as String)
                        val itemAssignStmt = Jimple.v().newAssignStmt(arrayRef, itemValue)
                        body.units.add(itemAssignStmt)
                    }
                }
            }
        }

        return arrayLocal
    }
}
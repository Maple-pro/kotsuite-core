package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.value.*
import soot.*
import soot.dava.internal.javaRep.DIntConstant
import soot.jimple.*

object ValueJimpleGenerator {
    private val log = LogManager.getLogger()

    fun generateJimpleValueFromChromosomeValue(body: Body, chromosomeValue: ChromosomeValue): Value {
        return when (chromosomeValue) {
            is BooleanChromosomeValue -> generateBooleanValue(chromosomeValue.booleanValue)
            is ByteChromosomeValue -> generateByteValue(chromosomeValue.byteValue)
            is CharChromosomeValue -> generateCharValue(chromosomeValue.charValue)
            is DoubleChromosomeValue -> generateDoubleValue(chromosomeValue.doubleValue)
            is FloatChromosomeValue -> generateFloatValue(chromosomeValue.floatValue)
            is IntChromosomeValue -> generateIntValue(chromosomeValue.intValue)
            is LongChromosomeValue -> generateLongValue(chromosomeValue.longValue)
            is ShortChromosomeValue -> generateShortValue(chromosomeValue.shortValue)
            is StringChromosomeValue -> generateStringValue(chromosomeValue.stringValue)
            is ArrayChromosomeValue<*> -> generateArrayValue(body, chromosomeValue)
            else -> {
                throw Exception("Unsupported value type: $chromosomeValue")
            }
        }
    }

    fun generatePrimTypeJimpleValue(type: Type, concreteValue: Any): Value {
        if (type !is PrimType) {
            log.error("The type is not PrimType: $type")
            throw Exception("The type is not PrimType: $type")
        }
        return when (type) {
            // PrimType
            is BooleanType -> {
                if (concreteValue !is Boolean) {
                    log.error("Wrong concrete value type: $concreteValue")
                    throw Exception("Wrong concrete value type: $concreteValue")
                }
                generateBooleanValue(concreteValue)
            }
            is ByteType -> {
                if (concreteValue !is Byte) {
                    log.error("Wrong concrete value type: $concreteValue")
                    throw Exception("Wrong concrete value type: $concreteValue")
                }
                generateByteValue(concreteValue)
            }
            is CharType -> {
                if (concreteValue !is Char) {
                    log.error("Wrong concrete value type: $concreteValue")
                    throw Exception("Wrong concrete value type: $concreteValue")
                }
                generateCharValue(concreteValue)
            }
            is DoubleType -> {
                if (concreteValue !is Double) {
                    log.error("Wrong concrete value type: $concreteValue")
                    throw Exception("Wrong concrete value type: $concreteValue")
                }
                generateDoubleValue(concreteValue)
            }
            is FloatType -> {
                if (concreteValue !is Float) {
                    log.error("Wrong concrete value type: $concreteValue")
                    throw Exception("Wrong concrete value type: $concreteValue")
                }
                generateFloatValue(concreteValue)
            }
            is IntType -> {
                if (concreteValue !is Int) {
                    log.error("Wrong concrete value type: $concreteValue")
                    throw Exception("Wrong concrete value type: $concreteValue")
                }
                generateIntValue(concreteValue)
            }
            is LongType -> {
                if (concreteValue !is Long) {
                    log.error("Wrong concrete value type: $concreteValue")
                    throw Exception("Wrong concrete value type: $concreteValue")
                }
                generateLongValue(concreteValue)
            }
            is ShortType -> {
                if (concreteValue !is Short) {
                    log.error("Wrong concrete value type: $concreteValue")
                    throw Exception("Wrong concrete value type: $concreteValue")
                }
                generateShortValue(concreteValue)
            }
            else -> {
                log.error("Unsupported PrimType: $type")
                throw Exception("Unsupported PrimType: $type")
            }
        }
    }

    private fun generateBooleanValue(booleanValue: Boolean): Value {
        val tmp = if (booleanValue) 1 else 0
        return DIntConstant.v(tmp, BooleanType.v())
    }

    private fun generateByteValue(byteValue: Byte): Value {
        return DIntConstant.v(byteValue.toInt(), ByteType.v())
    }

    private fun generateCharValue(charValue: Char): Value {
        return DIntConstant.v(charValue.code, CharType.v())
    }

    private fun generateDoubleValue(doubleValue: Double): Value {
        return DoubleConstant.v(doubleValue)
    }

    private fun generateFloatValue(floatValue: Float): Value {
        return FloatConstant.v(floatValue)
    }

    private fun generateIntValue(intValue: Int): Value {
        return IntConstant.v(intValue)
    }

    private fun generateLongValue(longValue: Long): Value {
        return LongConstant.v(longValue)
    }

    private fun generateShortValue(shortValue: Short): Value {
        return DIntConstant.v(shortValue.toInt(), ShortType.v())
    }

    private fun generateStringValue(stringValue: String): Value {
        return StringConstant.v(stringValue)
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
            when (value.arrayType.baseType) {
                is PrimType -> {
                    val itemValue = generatePrimTypeJimpleValue(value.arrayType.baseType, item)
                    val itemAssignStmt = Jimple.v().newAssignStmt(arrayRef, itemValue)
                    body.units.add(itemAssignStmt)
                }
                is RefType -> {
                    if (value.arrayType.baseType == RefType.v("java.lang.String")) {
                        val itemValue = generateStringValue(item as String)
                        val itemAssignStmt = Jimple.v().newAssignStmt(arrayRef, itemValue)
                        body.units.add(itemAssignStmt)
                    }
                }
            }
        }

        return arrayLocal
    }
}
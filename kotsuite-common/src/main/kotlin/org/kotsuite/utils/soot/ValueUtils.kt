package org.kotsuite.utils.soot

import org.apache.logging.log4j.LogManager
import org.kotsuite.utils.RandomUtils
import soot.*
import soot.dava.internal.javaRep.DIntConstant
import soot.jimple.*

object ValueUtils {
    private val log = LogManager.getLogger()

    fun PrimType.generateRandomValue(): Value {
        return when (this) {
            is BooleanType -> generateBooleanValue(RandomUtils.generateBoolean())
            is ByteType -> generateByteValue(RandomUtils.generateByte())
            is CharType -> generateCharValue(RandomUtils.generateChar())
            is DoubleType -> generateDoubleValue(RandomUtils.generateDouble())
            is FloatType -> generateFloatValue(RandomUtils.generateFloat())
            is IntType -> generateIntValue(RandomUtils.generateInt())
            is LongType -> generateLongValue(RandomUtils.generateLong())
            is ShortType -> generateShortValue(RandomUtils.generateShort())
            else -> {
                log.error("Unsupported PrimType: $this")
                throw Exception("Unsupported PrimType: $this")
            }
        }
    }

    fun ArrayType.generateRandomValue(): Value {
        TODO()
    }

    fun PrimType.generatePrimTypeJimpleValue(concreteValue: Any): Value {
        return when (this) {
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
                log.error("Unsupported PrimType: $this")
                throw Exception("Unsupported PrimType: $this")
            }
        }
    }

    fun generateBooleanValue(booleanValue: Boolean): Value {
        val tmp = if (booleanValue) 1 else 0
        return DIntConstant.v(tmp, BooleanType.v())
    }

    fun generateByteValue(byteValue: Byte): Value {
        return DIntConstant.v(byteValue.toInt(), ByteType.v())
    }

    fun generateCharValue(charValue: Char): Value {
        return DIntConstant.v(charValue.code, CharType.v())
    }

    fun generateDoubleValue(doubleValue: Double): Value {
        return DoubleConstant.v(doubleValue)
    }

    fun generateFloatValue(floatValue: Float): Value {
        return FloatConstant.v(floatValue)
    }

    fun generateIntValue(intValue: Int): Value {
        return IntConstant.v(intValue)
    }

    fun generateLongValue(longValue: Long): Value {
        return LongConstant.v(longValue)
    }

    fun generateShortValue(shortValue: Short): Value {
        return DIntConstant.v(shortValue.toInt(), ShortType.v())
    }

    fun generateStringValue(stringValue: String): Value {
        return StringConstant.v(stringValue)
    }
}
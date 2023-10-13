package org.kotsuite.ga.strategy.random

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.value.*
import org.kotsuite.ga.chromosome.value.Value
import soot.*
import kotlin.random.Random

object ValueGenerator {

    private val log = LogManager.getLogger()

    private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + '+'

    @Throws(Exception::class)
    fun generatePrimValue(primType: PrimType): Value {
        return when (primType) {
            is BooleanType -> generateBooleanValue()
            is ByteType -> generateByteValue()
            is CharType -> generateCharValue()
            is DoubleType -> generateDoubleValue()
            is FloatType -> generateFloatValue()
            is IntType -> generateIntValue()
            is LongType -> generateLongValue()
            is ShortType -> generateShortValue()
            else -> {
                log.error("Unsupported value type: $primType")
                throw Exception("Unsupported value type: $primType")
            }
        }
    }

    fun generateStringValue(): Value {
        val len = Random.nextInt(100)
        var str = ""
        if (len != 0) {
            str = (0 until len).map {
                Random.nextInt(charPool.size).let { charPool[it] }
            }.joinToString("")
        }

        return StringValue(str)
    }

    fun generateArrayValue (arrayType: ArrayType): ArrayValue<out Any> {
        return when(arrayType.baseType) {
            is PrimType -> {
                ArrayValue(generateRandomPrimArray(arrayType.baseType), arrayType)
            }
            RefType.v("java.lang.String") -> {
                val stringArray = arrayOf("")
                ArrayValue(stringArray, arrayType)
            }
            else -> {
                // TODO: deal with more ref array type
                ArrayValue(arrayOf(), arrayType)
            }
        }
    }

    private fun generateBooleanValue(): BooleanValue {
        return BooleanValue(generateBoolean())
    }

    private fun generateByteValue(): ByteValue {
        return ByteValue(generateByte())
    }

    private fun generateCharValue(): CharValue {
        return CharValue(generateChar())
    }

    private fun generateDoubleValue(): DoubleValue{
        return DoubleValue(generateDouble())
    }

    private fun generateFloatValue(): FloatValue {
        return FloatValue(generateFloat())
    }

    private fun generateIntValue(): IntValue {
        return IntValue(generateInt())
    }

    private fun generateLongValue(): LongValue {
        return LongValue(generateLong())
    }

    private fun generateShortValue(): ShortValue {
        return ShortValue(generateShort())
    }

    private fun generateRandomPrimArray(type: Type): Array<Any> {
        val maxLen = 10
        val len = Random.nextInt(maxLen)

        return when(type) {
            is BooleanType -> {
                (0..len).map { generateBoolean() }.toTypedArray()
            }
            is ByteType -> {
                (0..len).map { generateByte() }.toTypedArray()
            }
            is CharType -> {
                (0..len).map { generateChar() }.toTypedArray()
            }
            is DoubleType -> {
                (0..len).map { generateDouble() }.toTypedArray()
            }
            is FloatType -> {
                (0..len).map { generateFloat() }.toTypedArray()
            }
            is IntType -> {
                (0..len).map { generateInt() }.toTypedArray()
            }
            is LongType -> {
                (0..len).map { generateLong() }.toTypedArray()
            }
            is ShortType -> {
                (0..len).map { generateShort() }.toTypedArray()
            }
            else -> {
                log.error("Unsupported value type: $type")
                throw Exception("Unsupported value type: $type")
            }
        }
    }

    private fun generateBoolean(): Boolean {
        val booleanValues = listOf(true, false)
        return booleanValues[Random.nextInt(0, 2)]
    }

    private fun generateByte(): Byte {
        return Random.nextBytes(1)[0]
    }

    private fun generateChar(): Char {
        return charPool[Random.nextInt(charPool.size)]
    }

    private fun generateDouble(): Double {
        val lowerBound = -100.0
        val upperBound = 100.0
        return Random.nextDouble(lowerBound, upperBound)
    }

    private fun generateFloat(): Float {
        val lowerBound = -100.0f
        val upperBound = 100.0f
        return Random.nextFloat() * (upperBound - lowerBound) + lowerBound
    }

    private fun generateInt(): Int {
        val lowerBound = -100
        val upperBound = 100
        return Random.nextInt(lowerBound, upperBound)
    }

    private fun generateLong(): Long {
        val lowerBound = -100L
        val upperBound = 100L
        return Random.nextLong(lowerBound, upperBound)
    }

    private fun generateShort(): Short {
        val lowerBound = -100
        val upperBound = 100
        return Random.nextInt(lowerBound, upperBound).toShort()
    }

}
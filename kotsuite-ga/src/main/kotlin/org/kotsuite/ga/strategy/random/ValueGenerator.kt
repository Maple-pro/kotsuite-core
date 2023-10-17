package org.kotsuite.ga.strategy.random

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.value.*
import org.kotsuite.ga.chromosome.value.ChromosomeValue
import org.kotsuite.utils.RandomUtils
import soot.*
import kotlin.random.Random

object ValueGenerator {

    private val log = LogManager.getLogger()

    private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + '+'

    @Throws(Exception::class)
    fun generatePrimValue(primType: PrimType): ChromosomeValue {
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

    fun generateStringValue(): ChromosomeValue {
        return StringChromosomeValue(RandomUtils.generateString())
    }

    fun generateArrayValue (arrayType: ArrayType): ArrayChromosomeValue<out Any> {
        return when(arrayType.baseType) {
            is PrimType -> {
                ArrayChromosomeValue(generateRandomPrimArray(arrayType.baseType), arrayType)
            }
            RefType.v("java.lang.String") -> {
                val stringArray = arrayOf("")
                ArrayChromosomeValue(stringArray, arrayType)
            }
            else -> {
                // TODO: deal with more ref array type
                ArrayChromosomeValue(arrayOf(), arrayType)
            }
        }
    }

    private fun generateBooleanValue(): BooleanChromosomeValue {
        return BooleanChromosomeValue(RandomUtils.generateBoolean())
    }

    private fun generateByteValue(): ByteChromosomeValue {
        return ByteChromosomeValue(RandomUtils.generateByte())
    }

    private fun generateCharValue(): CharChromosomeValue {
        return CharChromosomeValue(RandomUtils.generateChar())
    }

    private fun generateDoubleValue(): DoubleChromosomeValue{
        return DoubleChromosomeValue(RandomUtils.generateDouble())
    }

    private fun generateFloatValue(): FloatChromosomeValue {
        return FloatChromosomeValue(RandomUtils.generateFloat())
    }

    private fun generateIntValue(): IntChromosomeValue {
        return IntChromosomeValue(RandomUtils.generateInt())
    }

    private fun generateLongValue(): LongChromosomeValue {
        return LongChromosomeValue(RandomUtils.generateLong())
    }

    private fun generateShortValue(): ShortChromosomeValue {
        return ShortChromosomeValue(RandomUtils.generateShort())
    }

    private fun generateRandomPrimArray(type: Type): Array<Any> {
        val maxLen = 10
        val len = Random.nextInt(maxLen)

        return when(type) {
            is BooleanType -> {
                (0..len).map { RandomUtils.generateBoolean() }.toTypedArray()
            }
            is ByteType -> {
                (0..len).map { RandomUtils.generateByte() }.toTypedArray()
            }
            is CharType -> {
                (0..len).map { RandomUtils.generateChar() }.toTypedArray()
            }
            is DoubleType -> {
                (0..len).map { RandomUtils.generateDouble() }.toTypedArray()
            }
            is FloatType -> {
                (0..len).map { RandomUtils.generateFloat() }.toTypedArray()
            }
            is IntType -> {
                (0..len).map { RandomUtils.generateInt() }.toTypedArray()
            }
            is LongType -> {
                (0..len).map { RandomUtils.generateLong() }.toTypedArray()
            }
            is ShortType -> {
                (0..len).map { RandomUtils.generateShort() }.toTypedArray()
            }
            else -> {
                log.error("Unsupported value type: $type")
                throw Exception("Unsupported value type: $type")
            }
        }
    }


}
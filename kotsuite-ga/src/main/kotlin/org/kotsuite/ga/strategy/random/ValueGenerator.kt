package org.kotsuite.ga.strategy.random

import org.kotsuite.ga.chromosome.value.*
import org.slf4j.LoggerFactory
import soot.ArrayType
import soot.BooleanType
import soot.DoubleType
import soot.IntType
import soot.PrimType
import soot.RefType
import kotlin.random.Random

object ValueGenerator {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun generatePrimValue(primType: PrimType): Value {
        return when (primType) {
            is IntType -> generateIntValue()
            is BooleanType -> generateBooleanValue()
            is DoubleType -> generateDoubleValue()
            else -> {
                // TODO
                logger.error("Unsupported value type: $primType")
                throw Exception("Unsupported value type: $primType")
            }
        }
    }

    fun generateStringValue(): Value {
        val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + '+'

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
            RefType.v("java.lang.Integer") -> {
                val randomArray = generateRandomIntArray()
                ArrayValue(randomArray)
            }
            RefType.v("java.lang.String") -> {
                val stringArray = arrayOf("")
                ArrayValue(stringArray)
            }
            else -> {
                // TODO
                throw Exception("Unsupported Array Type: $arrayType")
            }
        }
    }

    private fun generateIntValue(): IntValue {
        val lowerBound = -100
        val upperBound = 100
        return IntValue(Random.nextInt(lowerBound, upperBound))
    }

    private fun generateBooleanValue(): BooleanValue{
        val booleanValues = listOf(true, false)
        return BooleanValue(booleanValues[Random.nextInt(0, 2)])
    }

    private fun generateDoubleValue(): DoubleValue{
        val lowerBound = -100.0
        val upperBound = 100.0
        return DoubleValue(Random.nextDouble(lowerBound, upperBound))
    }

    private fun generateRandomIntArray(): Array<Int> {
        val maxLen = 10
        val lowerBound = -100
        val upperBound = 100

        val len = Random.nextInt(maxLen)
        return (0..len).map { Random.nextInt(lowerBound, upperBound) }.toTypedArray()
    }

}
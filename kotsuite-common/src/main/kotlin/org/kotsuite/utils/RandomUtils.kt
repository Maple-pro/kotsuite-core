package org.kotsuite.utils

import kotlin.random.Random

object RandomUtils {
    private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + '+'

    fun generateBoolean(): Boolean {
        val booleanValues = listOf(true, false)
        return booleanValues[Random.nextInt(0, 2)]
    }

    fun generateByte(): Byte {
        return Random.nextBytes(1)[0]
    }

    fun generateChar(): Char {
        return charPool[Random.nextInt(charPool.size)]
    }

    fun generateDouble(): Double {
        val lowerBound = -100.0
        val upperBound = 100.0
        return Random.nextDouble(lowerBound, upperBound)
    }

    fun generateFloat(): Float {
        val lowerBound = -100.0f
        val upperBound = 100.0f
        return Random.nextFloat() * (upperBound - lowerBound) + lowerBound
    }

    fun generateInt(): Int {
        val lowerBound = -100
        val upperBound = 100
        return Random.nextInt(lowerBound, upperBound)
    }

    fun generateLong(): Long {
        val lowerBound = -100L
        val upperBound = 100L
        return Random.nextLong(lowerBound, upperBound)
    }

    fun generateShort(): Short {
        val lowerBound = -100
        val upperBound = 100
        return Random.nextInt(lowerBound, upperBound).toShort()
    }

    fun generateString(): String {
        val len = Random.nextInt(100)
        var str = ""
        if (len != 0) {
            str = (0 until len).map {
                Random.nextInt(charPool.size).let { charPool[it] }
            }.joinToString("")
        }

        return str
    }
}
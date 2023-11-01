package org.kotsuite.ga.chromosome.parameter

import soot.ArrayType

class ArrayParameter(val arrayType: ArrayType, valueIndex: Int): Parameter(valueIndex) {
    override fun toString(): String {
        return "array"
    }
}
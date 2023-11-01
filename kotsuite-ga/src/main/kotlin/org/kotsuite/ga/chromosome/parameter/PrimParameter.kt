package org.kotsuite.ga.chromosome.parameter

import soot.PrimType

class PrimParameter(
    val primType: PrimType, valueIndex: Int
): Parameter(valueIndex) {
    override fun toString(): String {
        return "$primType"
    }
}

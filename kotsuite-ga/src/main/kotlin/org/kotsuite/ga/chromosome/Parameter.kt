package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.type.ParameterType
import soot.PrimType

class Parameter(val parameterType: ParameterType) {
    // ParameterType.BUILTIN_TYPE
    var primType: PrimType? = null
    var valueIndex: Int? = null

    // ParameterType.VARIABLE
    val variable: Variable? = null
}
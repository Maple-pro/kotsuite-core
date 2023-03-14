package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.type.ParameterType
import soot.PrimType

class Parameter(val parameterType: ParameterType) {
    // ParameterType.BUILTIN_TYPE
    var primType: PrimType? = null
    var valueIndex: Int = -1

    // ParameterType.VARIABLE
    var variable: Variable? = null
}
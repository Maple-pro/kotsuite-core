package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.type.BuiltInType
import org.kotsuite.ga.chromosome.type.ParameterType

class Parameter(val parameterType: ParameterType) {
    val builtinType: BuiltInType? = null
    val variable: Variable? = null
}
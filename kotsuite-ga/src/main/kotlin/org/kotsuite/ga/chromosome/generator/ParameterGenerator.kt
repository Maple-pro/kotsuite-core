package org.kotsuite.ga.chromosome.generator

import org.kotsuite.ga.chromosome.parameter.*
import org.kotsuite.ga.chromosome.value.Value
import org.kotsuite.ga.utils.SootUtils
import soot.SootMethod

object ParameterGenerator {
    fun generate(parameter: Parameter, values: List<Value>, sootMethod: SootMethod): soot.Value {
        return when (parameter) {
            is PrimParameter -> {
                ValueGenerator.generate(values[parameter.valueIndex])
            }
            is StringParameter -> {
                ValueGenerator.generate(values[parameter.valueIndex])
            }
            is ArrayParameter -> {
                ValueGenerator.generate(values[parameter.valueIndex])
            }
            is RefTypeParameter -> {
                SootUtils.getLocalByName(sootMethod, parameter.variable.localName)
            }
            else -> {
                throw Exception("Unsupported")
            }
        }
    }
}
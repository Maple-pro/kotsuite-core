package org.kotsuite.ga.chromosome.jimple

import org.kotsuite.ga.chromosome.parameter.*
import org.kotsuite.ga.chromosome.value.Value
import org.kotsuite.utils.SootUtils
import soot.SootMethod

object ParameterJimpleGenerator {
    fun generate(parameter: Parameter, values: List<Value>, sootMethod: SootMethod): soot.Value {
        return when (parameter) {
            is PrimParameter -> {
                ValueJimpleGenerator.generate(values[parameter.valueIndex])
            }
            is StringParameter -> {
                ValueJimpleGenerator.generate(values[parameter.valueIndex])
            }
            is ArrayParameter -> {
                ValueJimpleGenerator.generate(values[parameter.valueIndex])
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
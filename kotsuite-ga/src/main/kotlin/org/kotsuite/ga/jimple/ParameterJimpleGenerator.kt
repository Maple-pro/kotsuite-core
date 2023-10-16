package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.parameter.*
import org.kotsuite.ga.chromosome.value.ChromosomeValue
import org.kotsuite.utils.SootUtils
import soot.SootMethod
import soot.Value

object ParameterJimpleGenerator {
    private val log = LogManager.getLogger()

    fun generate(parameter: Parameter, values: List<ChromosomeValue>, sootMethod: SootMethod): Value {
        return when (parameter) {
            is PrimParameter, is StringParameter, is ArrayParameter -> {
                ValueJimpleGenerator.generateJimpleValueFromChromosomeValue(sootMethod.activeBody, values[parameter.valueIndex])
            }
            is RefTypeParameter -> {
                SootUtils.getLocalByName(sootMethod, parameter.variable.localName)
            }
            else -> {
                log.error("Unsupported parameter type: $parameter")
                throw Exception()
            }
        }
    }
}
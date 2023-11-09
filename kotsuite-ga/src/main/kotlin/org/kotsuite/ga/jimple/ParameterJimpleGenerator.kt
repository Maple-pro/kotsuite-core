package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.exception.LocalNotFoundException
import org.kotsuite.exception.UnsupportedTypeException
import org.kotsuite.ga.chromosome.parameter.*
import org.kotsuite.ga.chromosome.value.ChromosomeValue
import org.kotsuite.soot.extensions.getLocalByName
import soot.SootMethod
import soot.Value

object ParameterJimpleGenerator {
    private val log = LogManager.getLogger()

    fun Parameter.generateJimpleValue(values: List<ChromosomeValue>, sootMethod: SootMethod): Value {
        return when (this) {
            is PrimParameter, is StringParameter, is ArrayParameter -> {
                ValueJimpleGenerator.generateJimpleValueFromChromosomeValue(sootMethod.activeBody, values[this.valueIndex])
            }
            is RefTypeParameter -> {
                val value = sootMethod.getLocalByName(this.variable.localName)
                return value ?: throw LocalNotFoundException("Cannot find local variable: ${this.variable.localName}")
            }
            else -> {
                log.error("Unsupported parameter type: $this")
                throw UnsupportedTypeException("Unsupported parameter type: $this")
            }
        }
    }
}
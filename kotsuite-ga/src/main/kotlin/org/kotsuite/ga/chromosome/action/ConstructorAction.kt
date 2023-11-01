package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.ga.chromosome.parameter.Parameter
import soot.SootClass
import soot.SootMethod

class ConstructorAction(
    val variable: Variable,
    val targetClass: SootClass,
    val constructor: SootMethod,
    parameters: List<Parameter>,
) : Action(parameters) {
    override fun toString(): String {
        val className = targetClass.shortName
        val objectName = variable.localName
        val parametersString = parameters.joinToString(", ") { it.toString() }
        return "$className $objectName = new $className($parametersString)"
    }
}
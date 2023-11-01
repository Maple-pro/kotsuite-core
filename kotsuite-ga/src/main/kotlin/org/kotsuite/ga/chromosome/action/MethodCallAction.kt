package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.ga.chromosome.parameter.Parameter
import soot.SootMethod

class MethodCallAction(
    val variable: Variable,
    val method: SootMethod,
    parameters: List<Parameter>,
): Action(parameters) {

    override fun toString(): String {
        val objectName = variable.localName
        val methodName = method.name
        val parametersString = parameters.joinToString(", ") { it.toString() }
        return "$objectName.$methodName($parametersString)"
    }
}
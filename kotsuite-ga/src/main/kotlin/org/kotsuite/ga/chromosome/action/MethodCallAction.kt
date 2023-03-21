package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.ga.chromosome.parameter.Parameter
import soot.SootMethod

class MethodCallAction(
    val variable: Variable, val method: SootMethod, parameters: MutableList<Parameter>
): Action(parameters)
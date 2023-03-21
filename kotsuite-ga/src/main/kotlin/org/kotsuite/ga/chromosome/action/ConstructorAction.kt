package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.ga.chromosome.parameter.Parameter
import soot.SootMethod

class ConstructorAction(
    val variable: Variable, val constructor: SootMethod, parameters: MutableList<Parameter>
): Action(parameters)
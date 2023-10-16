package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.ga.chromosome.parameter.Parameter
import soot.SootMethod

class MockWhenAction(
    val variable: Variable,
    val mockMethod: SootMethod,
    parameter: MutableList<Parameter>,
    val returnValue: Any,
): Action(parameter)
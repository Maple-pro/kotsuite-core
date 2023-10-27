package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.ga.chromosome.parameter.Parameter
import org.kotsuite.soot.MockWhenActionType
import soot.SootMethod

class MockWhenAction(
    val mockWhenActionType: MockWhenActionType,
    val variable: Variable,
    val mockMethod: SootMethod,
    parameter: MutableList<Parameter>,
    val returnValue: Any,
): Action(parameter)
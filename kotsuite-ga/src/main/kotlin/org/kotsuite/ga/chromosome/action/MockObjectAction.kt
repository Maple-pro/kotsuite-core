package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.ga.chromosome.parameter.Parameter
import soot.SootClass

class MockObjectAction(
    val variable: Variable,
    val mockType: InitializationType,
    val mockClass: SootClass,
    parameters: MutableList<Parameter>,
) : Action(parameters)
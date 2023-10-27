package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.ga.chromosome.parameter.Parameter
import org.kotsuite.soot.TestDoubleType
import soot.SootClass

class TestDoubleAction(
    val variable: Variable,
    val testDoubleType: TestDoubleType,
    val mockClass: SootClass,
    parameters: MutableList<Parameter>,
) : Action(parameters)
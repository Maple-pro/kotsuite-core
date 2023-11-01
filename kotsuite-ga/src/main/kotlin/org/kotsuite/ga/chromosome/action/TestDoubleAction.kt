package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.ga.chromosome.parameter.Parameter
import org.kotsuite.soot.TestDoubleType
import soot.SootClass

open class TestDoubleAction(
    val variable: Variable,
    val testDoubleType: TestDoubleType,
    val mockClass: SootClass,
    parameters: List<Parameter>,
) : Action(parameters)
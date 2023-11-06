package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.soot.TestDoubleType
import soot.SootClass

class JSpykTestDoubleAction(
    variable: Variable,
    val spykObject: Variable,
    mockClass: SootClass,
) : TestDoubleAction(variable, TestDoubleType.JMOCKK_SPY, mockClass, listOf()) {
    override fun toString(): String {
        val className = mockClass.shortName
        val objectName = variable.localName
        val spykObjectName = spykObject.localName

        return "$className $objectName = spyk($spykObjectName)"
    }
}
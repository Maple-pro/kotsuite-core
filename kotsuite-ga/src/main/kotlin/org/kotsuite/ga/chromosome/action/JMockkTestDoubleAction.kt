package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import org.kotsuite.soot.TestDoubleType
import soot.SootClass

class JMockkTestDoubleAction(
    variable: Variable,
    mockClass: SootClass,
    val relaxed: Boolean,
) : TestDoubleAction(variable, TestDoubleType.JMOCKK_MOCK, mockClass, listOf()) {
    override fun toString(): String {
        val className = mockClass.shortName
        val objectName = variable.localName
        val parameterString = "$className.class, relaxed = $relaxed"
        return "$className $objectName = mockk($parameterString)"
    }
}
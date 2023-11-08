package org.kotsuite.ga.chromosome.action

import org.kotsuite.ga.chromosome.Variable
import soot.SootClass

class GetInstanceAction(
    val variable: Variable,
    val targetClass: SootClass,
) : Action(parameters = listOf()) {
    override fun toString(): String {
        val className = targetClass.shortName
        val objectName = variable.localName

        return "$className $objectName = $className.INSTANCE"
    }
}
package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.type.ActionType

class Action(val actionType: ActionType): Element() {
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }

    val variable = Variable("")

    val constructor: String? = null  // CONSTRUCTOR
    val clazz: String? = null  // NULL_ASSIGNMENT
    val method: String? = null  // METHOD_CALL
    val parameters = mutableListOf<Parameter>()  // METHOD_CALL
}
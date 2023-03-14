package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.type.ActionType
import soot.SootClass
import soot.SootMethod

class Action(val actionType: ActionType): Element() {
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }

    var variable = Variable("", null)

    var constructor: SootMethod? = null  // CONSTRUCTOR
    var clazz: SootClass? = null  // NULL_ASSIGNMENT
    var method: SootMethod? = null  // METHOD_CALL
    var parameters = mutableListOf<Parameter>()  // METHOD_CALL
}
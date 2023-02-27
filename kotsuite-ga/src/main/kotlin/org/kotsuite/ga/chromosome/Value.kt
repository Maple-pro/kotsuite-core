package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.type.BuiltInType

class Value(val builtInType: BuiltInType): Element() {
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }

    var value: Any? = null
}
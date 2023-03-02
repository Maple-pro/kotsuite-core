package org.kotsuite.ga.chromosome

import soot.PrimType

class Value(val primType: PrimType): Element() {
    override fun accept(visitor: ElementVisitor) {
        visitor.visit(this)
    }

    var value: Any? = null
}
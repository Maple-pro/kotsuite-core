package org.kotsuite.ga.chromosome

abstract class Element {
    abstract fun accept(visitor: ElementVisitor)
}
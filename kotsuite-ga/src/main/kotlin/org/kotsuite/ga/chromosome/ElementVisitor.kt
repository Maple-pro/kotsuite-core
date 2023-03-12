package org.kotsuite.ga.chromosome

import soot.SootClass

interface ElementVisitor {
    fun visit(element: TestClass): SootClass

    fun visit(element: TestCase)

    fun visit(element: Action)
}
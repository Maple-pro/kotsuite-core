package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.Action
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.chromosome.TestClass

interface ElementVisitor {
    fun visit(element: TestClass)

    fun visit(element: TestCase)

    fun visit(element: Action)

    fun visit(element: Value)
}
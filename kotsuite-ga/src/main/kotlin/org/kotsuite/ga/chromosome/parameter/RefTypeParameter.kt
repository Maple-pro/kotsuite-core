package org.kotsuite.ga.chromosome.parameter

import org.kotsuite.ga.chromosome.Variable

class RefTypeParameter(val variable: Variable): Parameter(-1) {
    override fun toString(): String {
        return variable.localName
    }
}
package org.kotsuite.soot

import soot.Body
import soot.Local
import soot.RefType

object JMockK {
    fun RefType.generateMockTestDouble(body: Body, localName: String, relaxed: Boolean): Local {
        TODO()
    }

    fun RefType.generateSpyTestDouble(body: Body, localName: String): Local {
        TODO()
    }

    fun Local.generateSpyTestDouble(body: Body, localName: String): Local {
        TODO()
    }

    /**
     * Generate mock when stmt
     *
     */
    fun generateMockWhenStmt() {
        TODO()
    }

}
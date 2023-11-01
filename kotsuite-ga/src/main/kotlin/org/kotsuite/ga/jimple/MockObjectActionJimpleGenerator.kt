package org.kotsuite.ga.jimple

import org.kotsuite.ga.chromosome.action.TestDoubleAction
import org.kotsuite.soot.Mockito.generateTestDouble
import soot.*

object MockObjectActionJimpleGenerator {

    /**
     * TODO: Not implemented yet
     * Generate mock object stmt
     *
     * @param body
     */
    fun TestDoubleAction.generateMockObjectStmt(body: Body) {
        this.mockClass.type.generateTestDouble(
            body,
            this.testDoubleType,
            this.variable.localName
        )
    }
}
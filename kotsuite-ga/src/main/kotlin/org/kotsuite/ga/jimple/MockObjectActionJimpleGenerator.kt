package org.kotsuite.ga.jimple

import org.kotsuite.ga.chromosome.action.TestDoubleAction
import org.kotsuite.soot.Mockito.generateTestDouble
import soot.*

object MockObjectActionJimpleGenerator {

    fun TestDoubleAction.generateMockObjectStmt(body: Body) {
        this.mockClass.type.generateTestDouble(
            body,
            this.testDoubleType,
            this.variable.localName
        )
    }
}
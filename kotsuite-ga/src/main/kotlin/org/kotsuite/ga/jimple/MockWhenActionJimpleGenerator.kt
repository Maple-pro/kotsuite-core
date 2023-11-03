package org.kotsuite.ga.jimple

import org.kotsuite.ga.chromosome.action.MockWhenAction
import org.kotsuite.ga.chromosome.value.ChromosomeValue
import org.kotsuite.ga.jimple.ParameterJimpleGenerator.generateJimpleValue
import org.kotsuite.soot.JMockK
import org.kotsuite.soot.MockWhenActionType
import org.kotsuite.soot.Mockito
import soot.*

object MockWhenActionJimpleGenerator {

    /**
     * Generate mock when statement
     */
    fun MockWhenAction.generateMockWhenStmt(
        body: Body,
        values: List<ChromosomeValue>,
        sootMethod: SootMethod,
    ) {
        when (this.mockWhenActionType) {
            MockWhenActionType.JMOCKK -> {
                // The parameters is always empty
//                val parameterValues = this.parameters.map { it.generateJimpleValue(values, sootMethod) }
                val returnValue = this.returnValue.generateJimpleValue(values, sootMethod)
                JMockK.generateMockWhenStmt(body, this.mockObject.localName, this.mockMethod, returnValue)
            }
            MockWhenActionType.MOCKITO -> {
                val thenReturnValue = this.returnValue.generateJimpleValue(values, sootMethod)
                Mockito.generateMockWhenStmt(body, this.mockObject.localName, this.mockMethod, thenReturnValue)
            }
        }
    }
}
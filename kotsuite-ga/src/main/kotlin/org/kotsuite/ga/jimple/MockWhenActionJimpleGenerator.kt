package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.MockitoConstants
import org.kotsuite.ga.chromosome.action.MockWhenAction
import org.kotsuite.ga.chromosome.parameter.Parameter
import org.kotsuite.ga.chromosome.value.ChromosomeValue
import org.kotsuite.ga.jimple.ParameterJimpleGenerator.generateJimpleValue
import org.kotsuite.soot.JMockK
import org.kotsuite.soot.MockWhenActionType
import org.kotsuite.soot.Mockito
import org.kotsuite.utils.IDUtils
import org.kotsuite.soot.Mockito.generateMockitoTestDouble
import org.kotsuite.soot.SootUtils.getLocalByName
import org.kotsuite.soot.TestDoubleType
import org.kotsuite.soot.Value.generateRandomValue
import soot.*
import soot.jimple.Jimple
import java.lang.IllegalArgumentException

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
                JMockK.generateMockWhenStmt(body)
            }
            MockWhenActionType.MOCKITO -> {
                val thenReturnValue = this.returnValue.generateJimpleValue(values, sootMethod)
                Mockito.generateMockWhenStmt(body, this.mockObject.localName, this.mockMethod, thenReturnValue)
            }
        }
    }
}
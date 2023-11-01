package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.action.JMockkTestDoubleAction
import org.kotsuite.ga.chromosome.action.JSpykTestDoubleAction
import org.kotsuite.ga.chromosome.action.TestDoubleAction
import org.kotsuite.soot.JMockK.generateJMockTestDouble
import org.kotsuite.soot.JMockK.generateJSpyTestDouble
import org.kotsuite.soot.Mockito.generateMockitoTestDouble
import org.kotsuite.soot.TestDoubleType
import soot.*

object TestDoubleActionJimpleGenerator {
    private val log = LogManager.getLogger()

    /**
     * Generate mock object stmt
     */
    fun TestDoubleAction.generateMockObjectStmt(body: Body) {
        when (this.testDoubleType) {
            TestDoubleType.MOCKITO_MOCK, TestDoubleType.MOCKITO_SPY -> {
                this.mockClass.type.generateMockitoTestDouble(
                    body,
                    this.testDoubleType,
                    this.variable.localName
                )
            }
            TestDoubleType.JMOCKK_MOCK -> {
                if (this !is JMockkTestDoubleAction) {
                    log.error("Wrong test double type")
                    throw Exception("Wrong test double type")
                }
                this.mockClass.type.generateJMockTestDouble(body, this.variable.localName, this.relaxed)
            }
            TestDoubleType.JMOCKK_SPY -> {
                if (this !is JSpykTestDoubleAction) {
                    log.error("Wrong test double type")
                    throw Exception("Wrong test double type")
                }
                this.mockClass.type.generateJSpyTestDouble(body, this.variable.localName, this.spykObject.localName)
            }
        }
    }
}
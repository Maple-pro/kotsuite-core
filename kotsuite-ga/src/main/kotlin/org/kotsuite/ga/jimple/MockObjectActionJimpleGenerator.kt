package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.action.InitializationType
import org.kotsuite.ga.chromosome.action.MockObjectAction
import org.kotsuite.utils.soot.MockType
import org.kotsuite.utils.soot.MockUtils.generateMockLocal
import soot.*
import java.lang.IllegalArgumentException

object MockObjectActionJimpleGenerator {
    private val log = LogManager.getLogger()

    fun MockObjectAction.generateMockObjectStmt(body: Body) {
        val mockType = when (this.mockType) {
            InitializationType.MOCK -> MockType.MOCK
            InitializationType.SPY -> MockType.SPY
            else -> {
                log.error("Initialization type can not be CONSTRUCTOR")
                throw IllegalArgumentException("Initialization type can not be CONSTRUCTOR")
            }
        }

        this.mockClass.type.generateMockLocal(
            body,
            mockType,
            this.variable.localName
        )
    }
}
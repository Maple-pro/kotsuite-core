package org.kotsuite.soot

import org.apache.logging.log4j.LogManager
import org.kotsuite.JMockKConstants
import soot.Body
import soot.Local
import soot.RefType
import soot.Scene
import soot.jimple.Jimple

object JMockK {
    private val log = LogManager.getLogger()
    private val jimple = Jimple.v()
    private val jmockkClass = Scene.v().getSootClass(JMockKConstants.jmockk_class_name)
    private val jmockkMockkMethod = Scene.v().getMethod(JMockKConstants.mockk_method_sig)
    private val jmockkSpykClassMethod = Scene.v().getMethod(JMockKConstants.spyk_class_method_sig)
    private val jmockkSpykObjectMethod = Scene.v().getMethod(JMockKConstants.spyk_object_method_sig)
    private val jmockkWhenMethod = Scene.v().getMethod(JMockKConstants.when_method_sig)
    private val jmockkThenReturnMethod = Scene.v().getMethod(JMockKConstants.thenReturn_method_sig)

    init {
        if (jmockkClass == null
            || jmockkMockkMethod == null
            || jmockkSpykClassMethod == null
            || jmockkSpykObjectMethod == null
            || jmockkWhenMethod == null
            || jmockkThenReturnMethod == null
        ) {
            log.error("Does not have JMockK dependency")
            throw Exception("Does not have JMockK dependency")
        }
    }

    fun RefType.generateJMockTestDouble(body: Body, localName: String, relaxed: Boolean): Local {
        TODO()
    }

    fun RefType.generateJSpyTestDouble(body: Body, localName: String, spyLocalName: String): Local {
        TODO()
    }

    /**
     * Generate mock when stmt
     *
     */
    fun generateMockWhenStmt(body: Body) {
        TODO()
    }

}
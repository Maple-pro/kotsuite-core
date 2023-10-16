package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.action.InitializationType
import org.kotsuite.ga.chromosome.action.MockObjectAction
import org.kotsuite.utils.ASMUtils.getClassDescriptor
import soot.*
import soot.jimple.ClassConstant
import soot.jimple.Jimple
import java.lang.IllegalArgumentException

object MockObjectActionJimpleGenerator {
    private val log = LogManager.getLogger()
    private val jimple = Jimple.v()
    private val mockitoClass: SootClass? = Scene.v().getSootClass("org.mockito.Mockito")

    fun generate(body: Body, mockObjectAction: MockObjectAction) {
        if (mockitoClass == null) {
            log.error("Does not have Mockito dependency")
            throw IllegalArgumentException("Does not have Mockito dependency")
        }

        val allocateObj = jimple.newLocal(mockObjectAction.variable.localName, mockObjectAction.variable.refType)

        body.locals.add(allocateObj)

        val mockMethodRef = when (mockObjectAction.mockType) {
            InitializationType.MOCK -> {
                mockitoClass.getMethod("java.lang.Object mock(java.lang.Class)").makeRef()
            }
            InitializationType.SPY -> {
                mockitoClass.getMethod("java.lang.Object spy(java.lang.Class)").makeRef()
            }
            else -> {
                log.error("Initialization type can not be CONSTRUCTOR")
                throw IllegalArgumentException("Initialization type can not be CONSTRUCTOR")
            }
        }

        val mockClassConstant = ClassConstant.v(mockObjectAction.mockClass.getClassDescriptor())
        val mockInvokeStmt = jimple.newInvokeStmt(
            jimple.newStaticInvokeExpr(mockMethodRef, mockClassConstant)
        )
        body.units.add(mockInvokeStmt)
    }
}
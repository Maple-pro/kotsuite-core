package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.action.MockWhenAction
import org.kotsuite.utils.IDUtils
import org.kotsuite.utils.soot.MockType
import org.kotsuite.utils.soot.MockUtils.generateMockLocal
import org.kotsuite.utils.soot.SootUtils.getLocalByName
import org.kotsuite.utils.soot.ValueUtils.generateRandomValue
import soot.*
import soot.jimple.Jimple
import java.lang.IllegalArgumentException

object MockWhenActionJimpleGenerator {
    private val log = LogManager.getLogger()
    private val jimple = Jimple.v()
    private val mockitoClass: SootClass? = Scene.v().getSootClass("org.mockito.Mockito")
    private const val MOCK_WHEN_METHOD_SIG = "<org.mockito.Mockito: org.mockito.stubbing.OngoingStubbing when(java.lang.Object)>"
    private const val THEN_RETURN_METHOD_SIG = "<org.mockito.stubbing.OngoingStubbing: java.lang.Object thenReturn(java.lang.Object)>"

    fun MockWhenAction.generateMockWhenStmt(body: Body) {
        if (mockitoClass == null) {
            log.error("Does not have Mockito dependency")
            throw IllegalArgumentException("Does not have Mockito dependency")
        }

        val targetMockMethodRef = this.mockMethod.makeRef()
        val mockWhenMethodRef = Scene.v().getMethod(MOCK_WHEN_METHOD_SIG).makeRef()
        val thenReturnMethodRef = Scene.v().getMethod(THEN_RETURN_METHOD_SIG).makeRef()

        val obj = body.getLocalByName(this.variable.localName) ?: return

        // invoke the target method, e.g., $i0 = virtualinvoke r1.<org.example.Grammar: int foo()>();
        val tempObj1 = jimple.newLocal("tempMockObj${IDUtils.getId()}", targetMockMethodRef.returnType)
        val targetMethodInvokeStmt = jimple.newAssignStmt(
            tempObj1,
            jimple.newVirtualInvokeExpr(obj, targetMockMethodRef)
        )

        // invoke the `mock` or `spy` method
        val tempObj2 = jimple.newLocal("tempMockObj${IDUtils.getId()}", RefType.v("org.mockito.stubbing.OngoingStubbing"))
        val mockWhenInvokeStmt = jimple.newAssignStmt(
            tempObj2,
            jimple.newStaticInvokeExpr(mockWhenMethodRef, tempObj1)
        )

        // invoke the `thenReturn` method
        val thenReturnValue = generateThenReturnValue(body, targetMockMethodRef.returnType)
        val thenReturnInvokeStmt = jimple.newInvokeStmt(
            jimple.newVirtualInvokeExpr(tempObj2, thenReturnMethodRef, thenReturnValue)
        )

        body.locals.addAll(listOf(tempObj1, tempObj2))
        body.units.addAll(listOf(targetMethodInvokeStmt, mockWhenInvokeStmt, thenReturnInvokeStmt))
    }

    private fun generateThenReturnValue(body: Body, type: Type): Value {
        return when (type) {
            is PrimType -> type.generateRandomValue()
            is RefType -> {
                val mockReturnValueLocalName = "" // TODO
                type.generateMockLocal(body, mockType = MockType.MOCK, localName = mockReturnValueLocalName)
            }
            is ArrayType -> type.generateRandomValue()
            else -> {
                log.error("Unsupported mock when return type: $type")
                throw IllegalArgumentException("Unsupported mock when return type: $type")
            }
        }
    }

}
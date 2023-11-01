package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.MockitoConstants
import org.kotsuite.ga.chromosome.action.MockWhenAction
import org.kotsuite.utils.IDUtils
import org.kotsuite.soot.Mockito.generateTestDouble
import org.kotsuite.soot.SootUtils.getLocalByName
import org.kotsuite.soot.TestDoubleType
import org.kotsuite.soot.Value.generateRandomValue
import soot.*
import soot.jimple.Jimple
import java.lang.IllegalArgumentException

object MockWhenActionJimpleGenerator {
    private val log = LogManager.getLogger()
    private val jimple = Jimple.v()
    private val mockitoClass: SootClass? = Scene.v().getSootClass(MockitoConstants.mockito_class_name)

    /**
     * Generate mock when statement
     *
     * TODO: 将 Soot 的具体逻辑抽象到 common 模块中
     *
     * @param body
     */
    fun MockWhenAction.generateMockWhenStmt(body: Body) {
        if (mockitoClass == null) {
            log.error("Does not have Mockito dependency")
            throw IllegalArgumentException("Does not have Mockito dependency")
        }

        val targetMockMethodRef = this.mockMethod.makeRef()
        val mockWhenMethodRef = Scene.v().getMethod(MockitoConstants.when_method_sig).makeRef()
        val thenReturnMethodRef = Scene.v().getMethod(MockitoConstants.thenReturn_method_sig).makeRef()

        val obj = body.getLocalByName(this.mockObject.localName) ?: return

        // invoke the target method, e.g., $i0 = virtualinvoke r1.<org.example.Grammar: int foo()>();
        val tempObj1 = jimple.newLocal("tempMockObj${IDUtils.getId()}", targetMockMethodRef.returnType)
        val targetMethodInvokeStmt = jimple.newAssignStmt(
            tempObj1,
            jimple.newVirtualInvokeExpr(obj, targetMockMethodRef)
        )

        // invoke the `mock` or `spy` method
        val tempObj2 = jimple.newLocal(
            "tempMockObj${IDUtils.getId()}",
            RefType.v(MockitoConstants.onGoingStubbing_class_name)
        )
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
                val mockReturnValueLocalName = "mockReturnValue${IDUtils.getId()}"
                type.generateTestDouble(
                    body,
                    mockType = TestDoubleType.MOCKITO_MOCK,
                    localName = mockReturnValueLocalName
                )
            }
            is ArrayType -> type.generateRandomValue()
            else -> {
                log.error("Unsupported mock when return type: $type")
                throw IllegalArgumentException("Unsupported mock when return type: $type")
            }
        }
    }

}
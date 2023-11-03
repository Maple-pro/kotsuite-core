package org.kotsuite.soot

import org.apache.logging.log4j.LogManager
import org.kotsuite.MockitoConstants
import org.kotsuite.ObjectConstants
import org.kotsuite.soot.SootUtils.getLocalByName
import org.kotsuite.utils.ASMUtils.getClassDescriptor
import org.kotsuite.utils.IDUtils
import soot.*
import soot.jimple.ClassConstant
import soot.jimple.Jimple
import java.lang.Exception

object Mockito {
    private val log = LogManager.getLogger()
    private val jimple = Jimple.v()
    private val mockitoClass: SootClass? = Scene.v().getSootClass(MockitoConstants.mockito_class_name)
    private val mockitoMockMethod: SootMethod? = Scene.v().getMethod(MockitoConstants.mock_method_sig)
    private val mockitoSpyMethod: SootMethod? = Scene.v().getMethod(MockitoConstants.spy_method_sig)
    private val mockitoWhenMethod: SootMethod? = Scene.v().getMethod(MockitoConstants.when_method_sig)
    private val mockitoThenReturnMethod: SootMethod? = Scene.v().getMethod(MockitoConstants.thenReturn_method_sig)

    init {
        if (mockitoClass == null
            || mockitoMockMethod == null
            || mockitoSpyMethod == null
            || mockitoWhenMethod == null
            || mockitoThenReturnMethod == null
        ) {
            log.error("Does not have Mockito dependency")
            throw Exception("Does not have Mockito dependency")
        }
    }

    /**
     * Generate Mockito mock local.
     *
     * @param body the method body which needs to generate mock local
     * @param mockType mock or spy
     * @param localName the name of the mock local variable
     * @return the mock local
     */
    fun RefType.generateMockitoTestDouble(
        body: Body,
        mockType: TestDoubleType,
        localName: String,
    ): Local {

        val allocateObj = jimple.newLocal(localName, this)
        val tempObj = jimple.newLocal(
            "tempMockObj${IDUtils.getId()}",
            RefType.v(ObjectConstants.OBJECT_CLASS_NAME)
        )

        body.locals.addAll(listOf(allocateObj, tempObj))

        val mockMethodRef = when (mockType) {
            TestDoubleType.MOCKITO_MOCK -> mockitoMockMethod!!.makeRef()
            TestDoubleType.MOCKITO_SPY -> mockitoSpyMethod!!.makeRef()
            else -> {
                log.error("Unsupported test double type: $mockType")
                throw Exception("Unsupported test double type: $mockType")
            }
        }

        val mockClassConstant = ClassConstant.v(this.sootClass.getClassDescriptor())
        val mockStmt = jimple.newAssignStmt(
            tempObj,
            jimple.newStaticInvokeExpr(mockMethodRef, mockClassConstant)
        )

        val castStmt = jimple.newAssignStmt(
            allocateObj,
            jimple.newCastExpr(tempObj, allocateObj.type)
        )
        body.units.addAll(listOf(mockStmt, castStmt))

        return allocateObj
    }

    /**
     * Generate mock when stmt
     */
    fun generateMockWhenStmt(
        body: Body,
        mockObjectLocalName: String,
        targetMethod: SootMethod,
        thenReturnValue: soot.Value,
    ) {
        val targetMockMethodRef = targetMethod.makeRef()
        val mockObject = body.getLocalByName(mockObjectLocalName) ?: return

        // invoke the target method
        val tempObj1 = jimple.newLocal("tempMockObj${IDUtils.getId()}", targetMockMethodRef.returnType)
        val targetMethodInvokeStmt = jimple.newAssignStmt(
            tempObj1,
            jimple.newVirtualInvokeExpr(mockObject, targetMockMethodRef)
        )

        // invoke the `when` method
        val tempObj2 = jimple.newLocal(
            "tempMockObj${IDUtils.getId()}",
            RefType.v(MockitoConstants.onGoingStubbing_class_name)
        )
        val mockWhenInvokeStmt = jimple.newAssignStmt(
            tempObj2,
            jimple.newStaticInvokeExpr(mockitoWhenMethod!!.makeRef(), tempObj1)
        )

        // invoke the `thenReturn` method
        val thenReturnInvokeStmt = jimple.newInvokeStmt(
            jimple.newVirtualInvokeExpr(tempObj2, mockitoThenReturnMethod!!.makeRef(), thenReturnValue)
        )

        body.locals.addAll(listOf(tempObj1, tempObj2))
        body.units.addAll(listOf(targetMethodInvokeStmt, mockWhenInvokeStmt, thenReturnInvokeStmt))
    }
}
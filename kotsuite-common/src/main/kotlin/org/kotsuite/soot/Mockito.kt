package org.kotsuite.soot

import org.apache.logging.log4j.LogManager
import org.kotsuite.MockitoConstants
import org.kotsuite.ObjectConstants
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

    /**
     * Generate mock local.
     *
     * @param body the method body which needs to generate mock local
     * @param mockType mock or spy
     * @param localName the name of the mock local variable, optional
     * @return the mock local
     */
    fun RefType.generateTestDouble(
        body: Body,
        mockType: TestDoubleType,
        localName: String,
    ): Local {
        if (mockitoClass == null || mockitoMockMethod == null || mockitoSpyMethod == null) {
            log.error("Does not have Mockito dependency")
            throw Exception("Does not have Mockito dependency")
        }

        val allocateObj = jimple.newLocal(localName, this)
        val tempObj = jimple.newLocal(
            "tempMockObj_${IDUtils.getId()}",
            RefType.v(ObjectConstants.OBJECT_CLASS_NAME)
        )

        body.locals.addAll(listOf(allocateObj, tempObj))

        val mockMethodRef = when (mockType) {
            TestDoubleType.MOCKITO_MOCK -> mockitoMockMethod.makeRef()
            TestDoubleType.MOCKITO_SPY -> mockitoSpyMethod.makeRef()
            else -> {
                TODO("Unimplemented yet")
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
     *
     */
    fun generateMockWhenStmt() {
        TODO()
    }

}
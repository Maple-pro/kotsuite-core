package org.kotsuite.utils.soot

import org.apache.logging.log4j.LogManager
import org.kotsuite.utils.ASMUtils.getClassDescriptor
import org.kotsuite.utils.IDUtils
import soot.*
import soot.jimple.ClassConstant
import soot.jimple.Jimple
import java.lang.Exception

object MockUtils {
    private val log = LogManager.getLogger()
    private val jimple = Jimple.v()
    private val mockitoClass: SootClass? = Scene.v().getSootClass("org.mockito.Mockito")
    private val mockMethod: SootMethod? = mockitoClass?.getMethod("java.lang.Object mock(java.lang.Class)")
    private val spyMethod: SootMethod? = mockitoClass?.getMethod("java.lang.Object spy(java.lang.Class)")

    /**
     * Generate mock local
     *
     * @param body the method body which needs to generate mock local
     * @param mockType mock or spy
     * @param localName the name of the mock local variable, optional
     * @return the mock local
     */
    fun RefType.generateMockLocal(
        body: Body,
        mockType: MockType,
        localName: String,
    ): Local {
        if (mockitoClass == null || mockMethod == null || spyMethod == null) {
            log.error("Does not have Mockito dependency")
            throw Exception("Does not have Mockito dependency")
        }

        val allocateObj = jimple.newLocal(localName, this)
        val tempObj = jimple.newLocal(
            "tempMockObj_${IDUtils.getId()}",
            RefType.v("java.lang.Object")
        )

        body.locals.addAll(listOf(allocateObj, tempObj))

        val mockMethodRef = when (mockType) {
            MockType.MOCK -> mockMethod.makeRef()
            MockType.SPY -> spyMethod.makeRef()
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
}
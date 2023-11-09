package org.kotsuite.soot

import org.apache.logging.log4j.LogManager
import org.kotsuite.CommonClassConstants
import org.kotsuite.JMockKConstants
import org.kotsuite.JMockKConstants.getVisibilityFieldSig
import org.kotsuite.exception.LocalNotFoundException
import org.kotsuite.exception.MissingDependencyException
import org.kotsuite.soot.extensions.getLocalByName
import org.kotsuite.soot.extensions.getInstanceName
import org.kotsuite.soot.extensions.getVisibility
import org.kotsuite.utils.ASMUtils.getClassDescriptor
import org.kotsuite.utils.IDUtils
import soot.*
import soot.dava.internal.javaRep.DIntConstant
import soot.jimple.ClassConstant
import soot.jimple.IntConstant
import soot.jimple.Jimple
import soot.jimple.StringConstant

object JMockK {
    private val log = LogManager.getLogger()

    private val jimple = Jimple.v()

    private val objectType = RefType.v(CommonClassConstants.object_class_name)

    private val jmockkClass = Scene.v().getSootClass(JMockKConstants.jmockk_class_name)
    private val jmockkMockkMethod = Scene.v().getMethod(JMockKConstants.mockk_method_sig)
    private val jmockkSpykClassMethod = Scene.v().getMethod(JMockKConstants.spyk_class_method_sig)
    private val jmockkSpykObjectMethod = Scene.v().getMethod(JMockKConstants.spyk_object_method_sig)
    private val jmockkWhenMethod = Scene.v().getMethod(JMockKConstants.when_method_sig)
    private val jmockkThenReturnMethod = Scene.v().getMethod(JMockKConstants.thenReturn_method_sig)
    private val jmockkOngoingStubbingClass = Scene.v().getSootClass(JMockKConstants.ongoingStubbing_class_name)
    private val jmockkVisibilityClass = Scene.v().getSootClass(JMockKConstants.visibility_class_name)

    init {
        if (jmockkClass == null
            || jmockkMockkMethod == null
            || jmockkSpykClassMethod == null
            || jmockkSpykObjectMethod == null
            || jmockkWhenMethod == null
            || jmockkThenReturnMethod == null
        ) {
            log.error("Does not have JMockK dependency")
            throw MissingDependencyException("Does not have JMockK dependency")
        }
    }

    /**
     * Builds a new mock for specified class.
     *
     * A mock is a fake version of a class that replaces all the methods with fake implementations.
     *
     * By default, every method that you wish to mock should be stubbed using [jmockkWhenMethod].
     * Otherwise, it will throw when called, so you know if you forgot to mock a method.
     * If [relaxed] is set to true, methods will automatically be stubbed.
     *
     * @param body the method body which needs to generate mock local
     * @param localName the name of the mock local variable
     * @param relaxed a relaxed mock is the mock that returns som simple value for all functions.
     * @return
     */
    fun RefType.generateJMockTestDouble(body: Body, localName: String, relaxed: Boolean): Local {
        val allocateObj = jimple.newLocal(localName, this)
        val tempObj = jimple.newLocal(
            "tempMockObj${IDUtils.getId()}",
            objectType
        )

        val mockClassConstant = ClassConstant.v(this.sootClass.getClassDescriptor())
        val booleanConstant = DIntConstant.v(if (relaxed) 1 else 0, BooleanType.v())

        val mockStmt = jimple.newAssignStmt(
            tempObj,
            jimple.newStaticInvokeExpr(
                jmockkMockkMethod!!.makeRef(), mockClassConstant, booleanConstant
            )
        )

        val castStmt = jimple.newAssignStmt(
            allocateObj,
            jimple.newCastExpr(tempObj, allocateObj.type)
        )

        body.locals.addAll(listOf(allocateObj, tempObj))
        body.units.addAll(listOf(mockStmt, castStmt))

        return allocateObj
    }

    /**
     * Builds a new spy for specified class, copying fields from [objectNameToSpy].
     *
     * A spy is a special kind of mock that enables a mix of mocked behavior and real behavior.
     *
     * A part of the behavior may be mocked using [jmockkWhenMethod],
     * but any non-mocked behavior will call tha original method.
     *
     * @param body the method body which needs to generate mock local
     * @param localName spyk name
     * @param objectNameToSpy the name of the object to spy
     * @return
     */
    fun RefType.generateJSpyTestDouble(body: Body, localName: String, objectNameToSpy: String): Local {
        val allocateObj = jimple.newLocal(localName, this)
        val tempObj = jimple.newLocal(
            "tempSpyObj${IDUtils.getId()}",
            objectType,
        )

        val spyLocal = body.getLocalByName(objectNameToSpy)
        if (spyLocal == null){
            val errorMsg = "Cannot find local variable: $objectNameToSpy"
            log.error(errorMsg)
            throw LocalNotFoundException(errorMsg)
        }

        val spyStmt = jimple.newAssignStmt(
            tempObj,
            jimple.newStaticInvokeExpr(
                jmockkSpykObjectMethod!!.makeRef(), spyLocal
            )
        )

        val castStmt = jimple.newAssignStmt(
            allocateObj,
            jimple.newCastExpr(tempObj, allocateObj.type)
        )

        body.locals.addAll(listOf(allocateObj, tempObj))
        body.units.addAll(listOf(spyStmt, castStmt))

        return allocateObj
    }

    /**
     * Generate mock when stmt
     *
     */
    fun generateMockWhenStmt(
        body: Body,
        mockObjectName: String,
        mockMethod: SootMethod,
        thenReturnValue: soot.Value,
    ) {
        // Get mockObject
        val mockObject = body.getLocalByName(mockObjectName)
        if (mockObject == null) {
            val errorMsg = "Cannot find local variable: $mockObjectName"
            log.error(errorMsg)
            throw LocalNotFoundException(errorMsg)
        }

        // Get methodVisibility
        val visibility = mockMethod.getVisibility()
        val visibilityFieldLocal = visibility.getVisibilityLocal(body)

        // Get methodName
        val methodNameConstant = StringConstant.v(mockMethod.name)

        // Get args
        val parameterArrayType = ArrayType.v(objectType, 1)
        val parameterArrayLocal = jimple.newLocal(
            "parameterArray${IDUtils.getId()}",
            parameterArrayType
        )
        val parameterArrayAssignStmt = jimple.newAssignStmt(
            parameterArrayLocal,
            jimple.newNewArrayExpr(
                objectType,
                IntConstant.v(0),
            )
        )

        // Get ongoingSubbing
        val ongoingSubbingLocal = jimple.newLocal(jmockkOngoingStubbingClass.getInstanceName(), jmockkOngoingStubbingClass.type)

        // Create when statement
        val whenStmt = jimple.newAssignStmt(
            ongoingSubbingLocal,
            jimple.newStaticInvokeExpr(
                jmockkWhenMethod!!.makeRef(), mockObject, visibilityFieldLocal, methodNameConstant, parameterArrayLocal,
            )
        )

        // Transform thenReturnValue to Object type
        val thenReturnValueObject = SootUtils.constantToObject(body, thenReturnValue)

        // Create thenReturn statement
        val thenReturnStmt = jimple.newInvokeStmt(
            jimple.newVirtualInvokeExpr(
                ongoingSubbingLocal,
                jmockkThenReturnMethod!!.makeRef(),
                thenReturnValueObject,
            )
        )

        body.locals.addAll(listOf(parameterArrayLocal, ongoingSubbingLocal))
        body.units.addAll(listOf(parameterArrayAssignStmt, whenStmt, thenReturnStmt))
    }

    /**
     * Transform [Visibility] to [Local] with `io.github.maples.jmockk.Visibility` type in soot.
     */
    private fun Visibility.getVisibilityLocal(body: Body): Local {
        val visibilityFieldSig = this.getVisibilityFieldSig()
        val visibilityFieldRef = Scene.v().getField(visibilityFieldSig).makeRef()
        val visibilityStaticFieldRef = jimple.newStaticFieldRef(visibilityFieldRef)
        val visibilityFieldLocal = jimple.newLocal(
            jmockkVisibilityClass.getInstanceName(),
            RefType.v(jmockkVisibilityClass)
        )

        val visibilityFieldAssignStmt = jimple.newAssignStmt(visibilityFieldLocal, visibilityStaticFieldRef)

        body.locals.add(visibilityFieldLocal)
        body.units.add(visibilityFieldAssignStmt)

        return visibilityFieldLocal
    }
}
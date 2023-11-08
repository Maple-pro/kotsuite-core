package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.action.*
import org.kotsuite.ga.chromosome.value.ChromosomeValue
import org.kotsuite.ga.jimple.TestDoubleActionJimpleGenerator.generateMockObjectStmt
import org.kotsuite.ga.jimple.MockWhenActionJimpleGenerator.generateMockWhenStmt
import org.kotsuite.ga.jimple.ParameterJimpleGenerator.generateJimpleValue
import org.kotsuite.soot.extensions.getInstanceLocal
import org.kotsuite.soot.extensions.getLocalByName
import soot.*
import soot.Unit
import soot.jimple.Jimple

object ActionJimpleGenerator {

    private val jimple = Jimple.v()
    private val log = LogManager.getLogger()

    /**
     * Generate jimple statement from Action
     *
     * @param body
     * @param values
     * @param sootMethod
     * @param collectReturnValue
     * @return if the action is the last action of the method, return the return value of the method
     */
    fun Action.generateJimpleStmt(
        body: Body,
        values: List<ChromosomeValue>,
        sootMethod: SootMethod,
        collectReturnValue: Boolean = false,
    ): Local? {
        var returnLocal: Local? = null

        val args = this.parameters.map {
            it.generateJimpleValue(values, sootMethod)
        }

        when (this) {
            is ConstructorAction -> {
                generateConstructorAction(body, this, args)
            }
            is GetInstanceAction -> {
                generateGetInstanceAction(body, this)
            }
            is TestDoubleAction -> {
                this.generateMockObjectStmt(body)
            }
            is MockWhenAction -> {
                this.generateMockWhenStmt(body, values, sootMethod)
            }
            is MethodCallAction -> {
                returnLocal = generateMethodCallAction(body, this, args, sootMethod, collectReturnValue)
            }
            else -> {
                log.error("Unimplemented action type: $this")
                throw Exception("Unimplemented action type: $this")
            }
        }

        return returnLocal
    }

    private fun generateConstructorAction(body: Body, action: ConstructorAction, args: List<Value>) {
        val sootClassType = RefType.v(action.constructor.declaringClass)  // sootClassType == action.variable.refType
        val allocateObj = jimple.newLocal(action.variable.localName, action.variable.refType)
        val allocateObjAssignStmt = jimple.newAssignStmt(allocateObj, jimple.newNewExpr(sootClassType))
        val invokeStmt = jimple.newInvokeStmt(
            jimple.newSpecialInvokeExpr(allocateObj, action.constructor.makeRef(), args)
        )

        body.locals.add(allocateObj)
        body.units.addAll(listOf(allocateObjAssignStmt, invokeStmt))
    }

    private fun generateGetInstanceAction(body: Body, action: GetInstanceAction) {
        val sootClass = action.targetClass
        sootClass.getInstanceLocal(body, action.variable.localName)
    }

    private fun generateMethodCallAction(
        body: Body,
        action: MethodCallAction, args: List<Value>, sootMethod: SootMethod,
        collectReturnValue: Boolean = false
    ): Local? {
        var returnLocal: Local? = null

        val locals = mutableListOf<Local>()
        val units = mutableListOf<Unit>()

        val obj = sootMethod.getLocalByName(action.variable.localName) ?: return null
        val invokeExpr = jimple.newVirtualInvokeExpr(obj, action.method.makeRef(), args)

        // assign the return value to a variable if exist
        if (collectReturnValue && action.method.returnType !is VoidType) {
            val capitalizedMethodName = action.method.name.replaceFirstChar { it.uppercase() }
            val returnValue = jimple.newLocal("returnValue$capitalizedMethodName", action.method.returnType)
            val assignStmt = jimple.newAssignStmt(returnValue, invokeExpr)

            locals.add(returnValue)
            units.add(assignStmt)

            returnLocal = returnValue
        } else {
            val invokeStmt = jimple.newInvokeStmt(invokeExpr)
            units.add(invokeStmt)
        }

        body.locals.addAll(locals)
        body.units.addAll(units)

        return returnLocal
    }

}
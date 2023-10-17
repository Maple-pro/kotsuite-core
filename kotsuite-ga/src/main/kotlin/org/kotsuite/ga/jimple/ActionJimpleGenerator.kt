package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.action.*
import org.kotsuite.ga.chromosome.parameter.ArrayParameter
import org.kotsuite.ga.chromosome.value.ChromosomeValue
import org.kotsuite.utils.soot.SootUtils.getLocalByName
import soot.*
import soot.Unit
import soot.jimple.Jimple

object ActionJimpleGenerator {

    private val jimple = Jimple.v()
    private val log = LogManager.getLogger()

    /**
     * Generate
     *
     * @param body
     * @param action
     * @param values
     * @param sootMethod
     * @param collectReturnValue
     * @return return local
     */
    @Throws(Exception::class)
    fun generate(
        body: Body,
        action: Action, values: List<ChromosomeValue>, sootMethod: SootMethod,
        collectReturnValue: Boolean = false
    ): Local? {
        var returnLocal: Local? = null

        val args = action.parameters.map {
            val value = ParameterJimpleGenerator.generate(it, values, sootMethod)
            if (it is ArrayParameter) {
                body.locals.add(value as Local)
            }

            value
        }

        when (action) {
            is ConstructorAction -> {
                generateConstructorAction(body, action, args)
            }
            is MockObjectAction -> {
                MockObjectActionJimpleGenerator.generate(body, action)
            }
            is MockWhenAction -> {
                MockWhenActionJimpleGenerator.generate(body, action)
            }
            is MethodCallAction -> {
                returnLocal = generateMethodCallAction(body, action, args, sootMethod, collectReturnValue)
            }
            else -> {
                log.error("Unimplemented action type: $action")
                throw Exception("Unimplemented action type: $action")
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
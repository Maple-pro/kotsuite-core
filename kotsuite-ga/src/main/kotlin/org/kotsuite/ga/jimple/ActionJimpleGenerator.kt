package org.kotsuite.ga.jimple

import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.chromosome.action.Action
import org.kotsuite.ga.chromosome.action.ConstructorAction
import org.kotsuite.ga.chromosome.action.MethodCallAction
import org.kotsuite.ga.chromosome.parameter.ArrayParameter
import org.kotsuite.ga.chromosome.value.Value
import org.kotsuite.utils.LocalsAndUnits
import org.kotsuite.utils.SootUtils
import soot.Local
import soot.RefType
import soot.SootMethod
import soot.Unit
import soot.VoidType
import soot.jimple.Jimple

object ActionJimpleGenerator {

    private val jimple = Jimple.v()
    private val log = LogManager.getLogger()

    @Throws(Exception::class)
    fun generate(
        action: Action, values: List<Value>, sootMethod: SootMethod,
        collectReturnValue: Boolean = false
    ): LocalsAndUnits {
        val locals = mutableListOf<Local>()
        val units = mutableListOf<Unit>()

        val args = action.parameters.map {
            val value = ParameterJimpleGenerator.generate(it, values, sootMethod)

            if (it is ArrayParameter) {
                locals.add(value as Local)
            }

            value
        }

        val actionLocalsAndUnits = when (action) {
            is ConstructorAction -> {
                generateConstructorAction(action, args)
            }
            is MethodCallAction -> generateMethodCallAction(action, args, sootMethod, collectReturnValue)
            else -> {
                log.error("Unimplemented action type: $action")
                throw Exception("Unimplemented action type: $action")
            }
        }

        locals.addAll(actionLocalsAndUnits.locals)
        units.addAll(actionLocalsAndUnits.units)

        return LocalsAndUnits(locals, units)
    }

    private fun generateConstructorAction(action: ConstructorAction, args: List<soot.Value>): LocalsAndUnits {
        val sootClassType = RefType.v(action.constructor.declaringClass)  // sootClassType == action.variable.refType
        val allocateObj = jimple.newLocal(action.variable.localName, action.variable.refType)
        val allocateObjAssignStmt = jimple.newAssignStmt(allocateObj, jimple.newNewExpr(sootClassType))
        val invokeStmt = jimple.newInvokeStmt(
            jimple.newSpecialInvokeExpr(allocateObj, action.constructor.makeRef(), args)
        )

        return LocalsAndUnits(
            listOf(allocateObj),
            listOf(allocateObjAssignStmt, invokeStmt)
        )
    }

    private fun generateMethodCallAction(
        action: MethodCallAction, args: List<soot.Value>, sootMethod: SootMethod,
        collectReturnValue: Boolean = false
    ): LocalsAndUnits {
        val locals = mutableListOf<Local>()
        val units = mutableListOf<Unit>()

        val obj = SootUtils.getLocalByName(sootMethod, action.variable.localName)
        val invokeExpr = jimple.newVirtualInvokeExpr(obj, action.method.makeRef(), args)

        // assign the return value to a variable if exist
        if (collectReturnValue && action.method.returnType !is VoidType) {
            val capitalizedMethodName = action.method.name.replaceFirstChar { it.uppercase() }
            val returnValue = jimple.newLocal("returnValue$capitalizedMethodName", action.method.returnType)
            val assignStmt = jimple.newAssignStmt(returnValue, invokeExpr)

            locals.add(returnValue)
            units.add(assignStmt)
        } else {
            val invokeStmt = jimple.newInvokeStmt(invokeExpr)
            units.add(invokeStmt)
        }

        return LocalsAndUnits(locals, units)
    }

}
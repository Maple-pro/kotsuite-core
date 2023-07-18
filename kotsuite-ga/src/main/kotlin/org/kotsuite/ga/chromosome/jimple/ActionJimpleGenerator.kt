package org.kotsuite.ga.chromosome.jimple

import org.kotsuite.ga.chromosome.action.Action
import org.kotsuite.ga.chromosome.action.ConstructorAction
import org.kotsuite.ga.chromosome.action.MethodCallAction
import org.kotsuite.ga.chromosome.value.Value
import org.kotsuite.ga.utils.SootUtils
import org.slf4j.LoggerFactory
import soot.Local
import soot.NullType
import soot.RefType
import soot.SootMethod
import soot.Type
import soot.Unit
import soot.VoidType
import soot.jimple.Jimple

object ActionJimpleGenerator {

    private val jimple = Jimple.v()
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun generate(action: Action, values: List<Value>, sootMethod: SootMethod, collectReturnValue: Boolean = false): LocalsAndUnits {
        val args = action.parameters.map {
            ParameterJimpleGenerator.generate(it, values, sootMethod)
        }

        val actionLocalsAndUnits = when (action) {
            is ConstructorAction -> generateConstructorAction(action, args)
            is MethodCallAction -> generateMethodCallAction(action, args, sootMethod, collectReturnValue)
            else -> {
                logger.error("Unimplemented action type: $action")
                throw Exception("Unimplemented action type: $action")
            }
        }

        return actionLocalsAndUnits
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
        action: MethodCallAction, args: List<soot.Value>, sootMethod: SootMethod, collectReturnValue: Boolean = false
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
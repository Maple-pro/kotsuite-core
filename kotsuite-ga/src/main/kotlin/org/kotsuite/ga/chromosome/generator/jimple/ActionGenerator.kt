package org.kotsuite.ga.chromosome.generator.jimple

import org.kotsuite.ga.chromosome.action.Action
import org.kotsuite.ga.chromosome.action.ConstructorAction
import org.kotsuite.ga.chromosome.action.MethodCallAction
import org.kotsuite.ga.chromosome.value.Value
import org.kotsuite.ga.utils.SootUtils
import org.slf4j.LoggerFactory
import soot.RefType
import soot.SootMethod
import soot.jimple.Jimple

object ActionGenerator {

    private val jimple = Jimple.v()
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun generate(action: Action, values: List<Value>, sootMethod: SootMethod): LocalsAndUnits {
        val args = action.parameters.map {
            ParameterGenerator.generate(it, values, sootMethod)
        }

        val actionLocalsAndUnits = when (action) {
            is ConstructorAction -> generateConstructorAction(action, args)
            is MethodCallAction -> generateMethodCallAction(action, args, sootMethod)
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
        action: MethodCallAction, args: List<soot.Value>, sootMethod: SootMethod
    ): LocalsAndUnits {
        val obj = SootUtils.getLocalByName(sootMethod, action.variable.localName)
        val invokeStmt = jimple.newInvokeStmt(
            jimple.newVirtualInvokeExpr(obj, action.method.makeRef(), args)
        )

        return LocalsAndUnits(
            listOf(),
            listOf(invokeStmt)
        )
    }

}
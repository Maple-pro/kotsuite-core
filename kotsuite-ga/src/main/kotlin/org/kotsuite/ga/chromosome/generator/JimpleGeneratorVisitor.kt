package org.kotsuite.ga.chromosome.generator

import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.chromosome.type.ActionType
import soot.Modifier
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.VoidType
import soot.jimple.IntConstant
import soot.jimple.Jimple
import soot.jimple.JimpleBody
import soot.jimple.Stmt
import java.lang.Exception
import soot.Value
import soot.jimple.internal.JimpleLocal

class JimpleGeneratorVisitor(jimpleFilesDir: String): ElementVisitor {


    override fun visit(element: TestClass): SootClass {
        // Resolve dependencies
        Scene.v().loadClassAndSupport("java.lang.Object")

        // Declare 'public class $element.testClassName'
        val sootClass = SootClass(element.testClassName, Modifier.PUBLIC)

        // extends Object
        sootClass.superclass = Scene.v().getSootClass("java.lang.Object")
        Scene.v().addClass(sootClass)

        // Create methods
        val sootMethods = element.testCases.map { createMethod(it, sootClass) }
        sootMethods.forEach { sootClass.addMethod(it) }

        return sootClass
    }

    private fun createMethod(testCase: TestCase, sootClass: SootClass): SootMethod {
        val method = SootMethod(testCase.testCaseName, null, VoidType.v())

        // Create the method body
        val body = Jimple.v().newBody(method)
        method.activeBody = body

        // Create this local
        val thisLocal = JimpleLocal("this", sootClass.type)
        body.locals.add(thisLocal)
        val thisStmt = Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(sootClass.type))
        body.units.add(thisStmt)

        testCase.actions.forEach { createStatement(it, method, testCase.values) }
        body.units.add(Jimple.v().newReturnVoidStmt())

        return method
    }

    private fun createStatement(action: Action, sootMethod: SootMethod, values: List<Value>){
        val body = sootMethod.activeBody
        val jimple = Jimple.v()

        when (action.actionType) {
            ActionType.CONSTRUCTOR -> {
                val sootClassType = RefType.v(action.constructor?.declaringClass)
                val allocatedObj = jimple.newLocal(action.variable.id, sootClassType)
                body.locals.add(allocatedObj)
                body.units.add(jimple.newAssignStmt(allocatedObj, Jimple.v().newNewExpr(sootClassType)))

                val constructorMethod = action.constructor
                val constructorArgs = action.parameters.map { values[it.valueIndex] }
                body.units.add(
                    jimple.newInvokeStmt(
                        jimple.newSpecialInvokeExpr(allocatedObj, constructorMethod?.makeRef(), constructorArgs)
                    )
                )
            }
            ActionType.METHOD_CALL -> {
                val methodArgs = action.parameters.map { values[it.valueIndex] }
                val allocatedObj = body.locals.last  // TODO: fix it
                body.units.add(
                    jimple.newInvokeStmt(
                        jimple.newVirtualInvokeExpr(allocatedObj, action.method?.makeRef(), methodArgs)
                    )
                )
            }
            ActionType.NULL_ASSIGNMENT -> {
                throw Exception("Not implement yet.")
            }
        }
    }

    override fun visit(element: TestCase) {
        TODO("Not yet implemented")
    }

    override fun visit(element: Action) {
        TODO("Not yet implemented")
    }
}
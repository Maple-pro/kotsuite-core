package org.kotsuite.ga.chromosome.generator

import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.chromosome.type.ActionType
import soot.ArrayType
import soot.Modifier
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.VoidType
import soot.jimple.Jimple
import java.lang.Exception
import soot.Value
import soot.jimple.NullConstant
import soot.jimple.internal.JimpleLocal
import java.util.Collections

class JimpleGeneratorVisitor: ElementVisitor {


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

        // Create <init> method
        val initMethod = createInitMethod(sootClass)
        sootClass.addMethod(initMethod)

        // Create main method
        val mainMethod = createMainMethod(sootMethods.first())
        sootClass.addMethod(mainMethod)

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

    private fun createInitMethod(sootClass: SootClass): SootMethod {
        val jimple = Jimple.v()

        val constructorMethod = SootMethod("<init>", null, VoidType.v(), Modifier.PUBLIC)

        val body = jimple.newBody(constructorMethod)
        constructorMethod.activeBody = body

        val thisLocal = jimple.newLocal("this", sootClass.type)
        body.locals.add(thisLocal)
        val thisStmt = jimple.newIdentityStmt(thisLocal, jimple.newThisRef(sootClass.type))
        body.units.add(thisStmt)

        body.units.add(jimple.newReturnVoidStmt())

        return constructorMethod
    }

    private fun createMainMethod(targetMethod: SootMethod): SootMethod {
        val argsParameterType = ArrayType.v(RefType.v("java.lang.String"), 1)
        val mainMethod = SootMethod("main",
            listOf(argsParameterType),
            VoidType.v(),
            Modifier.PUBLIC or Modifier.STATIC
        )

        val jimple = Jimple.v()
        val jimpleBody = jimple.newBody(mainMethod)
        mainMethod.activeBody = jimpleBody
        val locals = jimpleBody.locals
        val units = jimpleBody.units

        val argsParameter = jimple.newLocal("args", argsParameterType)
        locals.add(argsParameter)
        units.add(jimple.newIdentityStmt(argsParameter, jimple.newParameterRef(argsParameterType, 0)))

        val targetClassType = RefType.v(targetMethod.declaringClass)
        val allocatedTargetObj = jimple.newLocal("dummyObj", targetClassType)
        locals.add(allocatedTargetObj)
        units.add(jimple.newAssignStmt(allocatedTargetObj, jimple.newNewExpr(targetClassType)))

        val constructorMethod = targetClassType.sootClass.getMethod("void <init>()")
        val constructorArgs = Collections.nCopies(constructorMethod.parameterCount, NullConstant.v())
        units.add(
            jimple.newInvokeStmt(
                jimple.newSpecialInvokeExpr(allocatedTargetObj, constructorMethod.makeRef(), constructorArgs)
            )
        )

        val targetMethodArgs = Collections.nCopies(targetMethod.parameterCount, NullConstant.v())
        units.add(
            jimple.newInvokeStmt(
                jimple.newVirtualInvokeExpr(allocatedTargetObj, targetMethod.makeRef(), targetMethodArgs)
            )
        )

        units.add(jimple.newReturnVoidStmt())

        return mainMethod
    }

    override fun visit(element: TestCase) {
        TODO("Not yet implemented")
    }

    override fun visit(element: Action) {
        TODO("Not yet implemented")
    }
}
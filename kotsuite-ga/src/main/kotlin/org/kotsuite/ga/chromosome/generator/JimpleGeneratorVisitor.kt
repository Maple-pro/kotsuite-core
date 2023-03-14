package org.kotsuite.ga.chromosome.generator

import org.kotsuite.ga.chromosome.*
import org.kotsuite.ga.chromosome.type.ActionType
import org.kotsuite.ga.chromosome.type.ParameterType
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
import soot.jimple.StringConstant
import soot.jimple.internal.JimpleLocal
import java.util.Collections
import kotlin.random.Random

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
        if (element.testClassName == "ExampleTest") {
            val mainMethod = createMainMethod(sootMethods)
            sootClass.addMethod(mainMethod)
        }

        return sootClass
    }

    private fun createMethod(testCase: TestCase, sootClass: SootClass): SootMethod {
        val method = SootMethod(testCase.testCaseName, null, VoidType.v())

        // Create the method body
        val body = Jimple.v().newBody(method)
        method.activeBody = body


        testCase.actions.forEach { createStatement(it, method, testCase.values) }

        // Create this local
        val thisLocal = JimpleLocal("this", sootClass.type)
        body.locals.addFirst(thisLocal)
        val thisStmt = Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(sootClass.type))
        body.units.addFirst(thisStmt)

        // Create `println()` statement
//        createPrintlnStmt(method.name, method)

        body.units.add(Jimple.v().newReturnVoidStmt())

        return method
    }

    private fun createStatement(action: Action, sootMethod: SootMethod, values: List<Value>){
        val body = sootMethod.activeBody
        val jimple = Jimple.v()

        val args = action.parameters.map {
            when(it.parameterType) {
                ParameterType.BUILTIN_TYPE -> {
                    values[it.valueIndex]
                }
                ParameterType.VARIABLE -> {
                    createParam(it, sootMethod)
                }
            }
        }

        when (action.actionType) {
            ActionType.CONSTRUCTOR -> {
                val sootClassType = RefType.v(action.constructor?.declaringClass)
                val allocatedObj = jimple.newLocal(action.variable.id, action.variable.refType)
                body.locals.add(allocatedObj)
                body.units.add(jimple.newAssignStmt(allocatedObj, Jimple.v().newNewExpr(sootClassType)))

                val constructorMethod = action.constructor
                body.units.add(
                    jimple.newInvokeStmt(
                        jimple.newSpecialInvokeExpr(allocatedObj, constructorMethod?.makeRef(), args)
                    )
                )
            }
            ActionType.METHOD_CALL -> {
                val allocatedObj = body.locals.last  // TODO: fix it
                body.units.add(
                    jimple.newInvokeStmt(
                        jimple.newVirtualInvokeExpr(allocatedObj, action.method?.makeRef(), args)
                    )
                )
            }
            ActionType.NULL_ASSIGNMENT -> {
                throw Exception("Not implement yet.")
            }
        }
    }

    private fun createParam(param: Parameter, sootMethod: SootMethod): Value {
        val localName = param.variable?.id
        val refType = param.variable?.refType

        return if (refType!!.equals("java.lang.String")) {
            val stringLocal = Jimple.v().newLocal(localName!!, refType)
            val randomStringConstant = generateRandomString()
            val stringConstantAssignStmt = Jimple.v().newAssignStmt(stringLocal, StringConstant.v(randomStringConstant))

            sootMethod.activeBody.locals.addFirst(stringLocal)
            sootMethod.activeBody.units.addFirst(stringConstantAssignStmt)

            stringLocal
        } else {
            createInstanceLocal(param, sootMethod)
        }
    }

    private fun createInstanceLocal(param: Parameter, sootMethod: SootMethod): Value {
        val localName = param.variable?.id
        val refType = param.variable?.refType

        val local = Jimple.v().newLocal(localName!!, refType)
        val newAssignStmt = Jimple.v().newAssignStmt(local, Jimple.v().newNewExpr(refType))
        val constructorMethod = refType!!.sootClass.getMethod("void <init>(java.lang.String)")

        val constructorArgs = constructorMethod.parameterTypes.map {
            if (it.equals(RefType.v("java.lang.String"))) {
                StringConstant.v(generateRandomString())
            } else {
                null
            }
        }

        val invokeStmt = Jimple.v().newInvokeStmt(
            Jimple.v().newSpecialInvokeExpr(local, constructorMethod.makeRef(), constructorArgs)
        )

        sootMethod.activeBody.locals.addFirst(local)
        sootMethod.activeBody.units.addFirst(invokeStmt)
        sootMethod.activeBody.units.addFirst(newAssignStmt)

        return local
    }

    private fun createPrintlnStmt(message: String, sootMethod: SootMethod) {
        val body = sootMethod.activeBody
        val jimple = Jimple.v()

        // Add message local
        val messageLocal = jimple.newLocal("message", RefType.v("java.lang.String"))
        body.locals.add(messageLocal)
        val messageAssign = jimple.newAssignStmt(messageLocal, StringConstant.v(message))
        body.units.add(messageAssign)

        // Add local: java.io.printStream tmpRef
        val tmpRef = jimple.newLocal("tmpRef", RefType.v("java.io.PrintStream"))
        body.locals.add(tmpRef)

        // Add `tmpRef = java.lang.System.out`,
        // note that System.out is an instance of java.io.printStream, and it is a static field of java.lang.System
        body.units.add(
            jimple.newAssignStmt(
                tmpRef,
                jimple.newStaticFieldRef((
                        Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef()
                )
        )))

        // Add `tmpRef.println(message)`
        val printStreamClass = Scene.v().getSootClass("java.io.PrintStream")
        val printlnMethod = printStreamClass.getMethod("void println(java.lang.String)")
        body.units.add(jimple.newInvokeStmt(
            jimple.newVirtualInvokeExpr(tmpRef, printlnMethod.makeRef(), messageLocal)
        ))
    }

    private fun createInitMethod(sootClass: SootClass): SootMethod {
        val jimple = Jimple.v()

        // Create a constructor method
        val constructorMethod = SootMethod("<init>", null, VoidType.v(), Modifier.PUBLIC)

        val body = jimple.newBody(constructorMethod)
        constructorMethod.activeBody = body

        // Add `this` local variable to the constructor body
        val thisLocal = jimple.newLocal("this", sootClass.type)
        body.locals.add(thisLocal)
        val thisStmt = jimple.newIdentityStmt(thisLocal, jimple.newThisRef(sootClass.type))
        body.units.add(thisStmt)

        // Call the superclass constructor using `super()`
        val objectClass = Scene.v().getSootClass("java.lang.Object")
        val objectConstructorRef = Scene.v().makeConstructorRef(objectClass, listOf())
        val superInvokeStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(thisLocal, objectConstructorRef))
        body.units.add(superInvokeStmt)

        // Add return void statement
        body.units.add(jimple.newReturnVoidStmt())

        return constructorMethod
    }

    private fun createMainMethod(targetMethods: List<SootMethod>): SootMethod {
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

        val targetClassType = RefType.v(targetMethods.first().declaringClass)
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

        targetMethods.forEach {
            val targetMethodArgs = Collections.nCopies(it.parameterCount, NullConstant.v())
            units.add(
                jimple.newInvokeStmt(
                    jimple.newVirtualInvokeExpr(allocatedTargetObj, it.makeRef(), targetMethodArgs)
                )
            )
        }

        units.add(jimple.newReturnVoidStmt())

        return mainMethod
    }

    private fun generateRandomString(): String {
        val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        val len = Random.nextInt(10)
        return if (len == 0) {
            ""
        } else {
            (1..len).map { Random.nextInt(0, charPool.size).let { charPool[it] } }.joinToString("")
        }
    }

    override fun visit(element: TestCase) {
        TODO("Not yet implemented")
    }

    override fun visit(element: Action) {
        TODO("Not yet implemented")
    }
}
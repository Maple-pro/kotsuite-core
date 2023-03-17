package org.kotsuite.ga.chromosome.generator.jimple

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
import soot.jimple.IntConstant
import soot.jimple.NullConstant
import soot.jimple.StringConstant
import soot.jimple.internal.JimpleLocal
import java.util.Collections
import kotlin.random.Random

object TestClassGenerator {

    private val jimple = Jimple.v()

    fun generate(element: TestClass): SootClass {
        // Resolve dependencies
        Scene.v().loadClassAndSupport("java.lang.Object")

        // Declare 'public class $element.testClassName'
        val sootClass = SootClass(element.testClassName, Modifier.PUBLIC)

        // extends Object
        sootClass.superclass = Scene.v().getSootClass("java.lang.Object")
        Scene.v().addClass(sootClass)

        // Create <init> method
        val initMethod = createInitMethod(sootClass)

        // Create methods
        val sootMethods = element.testCases.map { TestCaseGenerator.generate(it, sootClass) }

        // Create main method
        var mainMethod: SootMethod? = null
        if (element.testClassName == "ExampleTest") {
            mainMethod = createMainMethod(sootMethods)
            sootClass.addMethod(mainMethod)
        }

        // Add methods to class
        val methods = listOf(initMethod) + sootMethods
        methods.forEach { sootClass.addMethod(it) }
        if (mainMethod != null) sootClass.addMethod(mainMethod)

        return sootClass
    }


    private fun createStatement(action: Action, sootMethod: SootMethod, values: List<Value>){  // TODO: refactor this
        val body = sootMethod.activeBody

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
                body.units.add(jimple.newAssignStmt(allocatedObj, jimple.newNewExpr(sootClassType)))

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
        val arrayType = param.variable?.arrayType

        if (refType == null && arrayType != null) {
            val baseType = arrayType.baseType
            if (baseType == RefType.v("java.lang.Integer")) {
                val randomArray = generateRandomIntArray()
                val stringArrayType = ArrayType.v(RefType.v("java.lang.Integer"), 1)
                val arrayLocal = jimple.newLocal(localName, stringArrayType)
                sootMethod.activeBody.locals.addFirst(arrayLocal)

                (randomArray.indices).forEach {
                    val arrayRef = jimple.newArrayRef(arrayLocal, IntConstant.v(it))
//                    val intLocal = jimple.newLocal("tmp_$it", RefType.v("java.lang.Integer"))
//                    val newAssignStmt = jimple.newAssignStmt(intLocal, jimple.newNewExpr(RefType.v("java.lang.Integer")))
//                    val intAssignStmt = jimple.newAssignStmt(intLocal, IntConstant.v(randomArray[it]))
//                    sootMethod.activeBody.locals.addFirst(intLocal)
//                    sootMethod.activeBody.units.addFirst(intAssignStmt)
//                    sootMethod.activeBody.units.addFirst(newAssignStmt)

//                    val assignStmt = jimple.newAssignStmt(arrayRef, intLocal)
                    val assignStmt = jimple.newAssignStmt(arrayRef, IntConstant.v(randomArray[it]))
                    sootMethod.activeBody.units.addFirst(assignStmt)
                }

                val arrayLocalAssignStmt = jimple.newAssignStmt(
                    arrayLocal,
                    jimple.newNewArrayExpr(
                        RefType.v("java.lang.Integer"), IntConstant.v(randomArray.size)
                    )
                )
                sootMethod.activeBody.units.addFirst(arrayLocalAssignStmt)

                return arrayLocal

            } else {
                throw Exception("Unsupported parameter type: $arrayType")
            }
        }

        return if (refType!! == RefType.v("java.lang.String")) {
            val stringLocal = jimple.newLocal(localName!!, refType)
            val randomStringConstant = generateRandomString()
            val stringConstantAssignStmt = jimple.newAssignStmt(stringLocal, StringConstant.v(randomStringConstant))

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

        val local = jimple.newLocal(localName!!, refType)
        val newAssignStmt = jimple.newAssignStmt(local, jimple.newNewExpr(refType))
        val constructorMethod = refType!!.sootClass.getMethod("void <init>(java.lang.String)")

        val constructorArgs = constructorMethod.parameterTypes.map {
            if (it.equals(RefType.v("java.lang.String"))) {
                StringConstant.v(generateRandomString())
            } else {
                null
            }
        }

        val invokeStmt = jimple.newInvokeStmt(
            jimple.newSpecialInvokeExpr(local, constructorMethod.makeRef(), constructorArgs)
        )

        sootMethod.activeBody.locals.addFirst(local)
        sootMethod.activeBody.units.addFirst(invokeStmt)
        sootMethod.activeBody.units.addFirst(newAssignStmt)

        return local
    }


    private fun createInitMethod(sootClass: SootClass): SootMethod {

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

//        val len = Random.nextInt(10)
        val len = if(Random.nextBoolean()) 0 else 10
        return if (len == 0) {
            ""
        } else {
            (1..len).map { Random.nextInt(0, charPool.size).let { charPool[it] } }.joinToString("")
        }
    }

    private fun generateRandomIntArray(): Array<Int> {
        val maxLen = 10
        val lowerBound = -100
        val upperBound = 100

        val len = Random.nextInt(maxLen)
        return (0..len).map { Random.nextInt(lowerBound, upperBound) }.toTypedArray()
    }

}
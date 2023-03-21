package org.kotsuite.ga.chromosome.generator.jimple

import org.kotsuite.ga.chromosome.*
import soot.ArrayType
import soot.Modifier
import soot.RefType
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.VoidType
import soot.jimple.Jimple
import soot.jimple.NullConstant
import java.util.Collections

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

        // Add methods to class
        val methods = listOf(initMethod) + sootMethods
        methods.forEach { sootClass.addMethod(it) }

        // Create main method
        val mainMethod: SootMethod?
        if (element.testClassName == "ExampleTest") {
            mainMethod = createMainMethod(sootMethods)
            sootClass.addMethod(mainMethod)
        }

        return sootClass
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

}
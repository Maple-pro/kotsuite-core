package org.kotsuite.ga.chromosome.generator.jimple

import soot.*
import soot.jimple.Jimple
import soot.jimple.NullConstant
import java.util.*

object Utils {

    fun generateMainClass(mainClassName: String, targetMethods: List<SootMethod>): SootClass {
        val existMainClass = Scene.v().classes.any { it.name == mainClassName }

        if (!existMainClass){
            Scene.v().loadClassAndSupport("java.lang.Object")

            val mainClass = SootClass(mainClassName, Modifier.PUBLIC)
            mainClass.superclass = Scene.v().getSootClass("java.lang.Object")
            Scene.v().addClass(mainClass)

            val mainMethod = createMainMethod(targetMethods)
            mainClass.addMethod(mainMethod)

            return mainClass
        } else {
            val mainClass = Scene.v().getSootClass(mainClassName)
            val mainMethod = mainClass.getMethodByName("main")
            val newMainMethod = createMainMethod(targetMethods)
            mainMethod.activeBody = newMainMethod.activeBody

            return mainClass
        }
    }

    private fun createMainMethod(targetMethods: List<SootMethod>): SootMethod {
        val jimple = Jimple.v()

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
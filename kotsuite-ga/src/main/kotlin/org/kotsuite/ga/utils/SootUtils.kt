package org.kotsuite.ga.utils

import org.kotsuite.ga.chromosome.jimple.LocalsAndUnits
import org.slf4j.LoggerFactory
import soot.*
import soot.jimple.Jimple
import soot.jimple.NullConstant
import java.util.*

object SootUtils {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Get local variable by variable name in a method
     *
     * @param sootMethod
     * @param localName
     * @return
     */
    fun getLocalByName(sootMethod: SootMethod, localName: String): Local {
        val body = sootMethod.activeBody
        var local: Local? = null
        for (l in body.locals) {
            if (l.name == localName) {
                local = l
                break
            }
        }

        if (local == null) {
            logger.error("Can't get local: $localName")
            throw Exception("Can't get local: $localName")
        }

        return local
    }

    /**
     * Get constructor of a soot class
     *
     * @param sootClass
     * @return
     */
    fun getConstructor(sootClass: SootClass): SootMethod? {
        return try {
//            sootClass.getMethod("void <init>()")
            sootClass.getMethodByName("<init>")
        } catch (ex: RuntimeException) {
            if (sootClass.methods.none { it.name == "<init>" }) {
                return null
            }
            sootClass.methods.first { it.name == "<init>" }
        }
    }

    /**
     * Generate main class. If the main class exist, it will replace the main method body.
     *
     * @param mainClassName main class name
     * @param targetMethods target methods which will be called in main method body
     * @return main class
     */
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

    /**
     * Create init method for a soot class
     *
     * @param sootClass
     * @return init method
     */
    fun createInitMethod(sootClass: SootClass): SootMethod {
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

    /**
     * Create main method which calls the target methods
     *
     * @param targetMethods methods that needs to be called
     * @return main method
     */
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

        val alreadyInstancedClasses = mutableListOf<String>()
        val instances = mutableListOf<Local>()
        targetMethods.forEach { targetMethod ->
            val targetClassFullName = targetMethod.declaringClass.name
            if (!alreadyInstancedClasses.contains(targetClassFullName)) { // If there is no instance of the target class
                val instanceLocalsAndUnits = generateInstance(targetMethod.declaringClass)

                locals.addAll(instanceLocalsAndUnits.locals)
                units.addAll(instanceLocalsAndUnits.units)

                alreadyInstancedClasses.add(targetClassFullName)

                instances.add(instanceLocalsAndUnits.locals.first())
            }

            val instance = instances.first { instance ->
                instance.type == targetMethod.declaringClass.type
            }

            val methodCallLocalsAndUnits = generateMethodCallStmt(targetMethod, instance)
            locals.addAll(methodCallLocalsAndUnits.locals)
            units.addAll(methodCallLocalsAndUnits.units)
        }

//        val targetClassType = RefType.v(targetMethods.first().declaringClass)
//        val allocatedTargetObj = jimple.newLocal("dummyObj", targetClassType)
//        locals.add(allocatedTargetObj)
//        units.add(jimple.newAssignStmt(allocatedTargetObj, jimple.newNewExpr(targetClassType)))
//
//        val constructorMethod = targetClassType.sootClass.getMethod("void <init>()")
//        val constructorArgs = Collections.nCopies(constructorMethod.parameterCount, NullConstant.v())
//        units.add(
//            jimple.newInvokeStmt(
//                jimple.newSpecialInvokeExpr(allocatedTargetObj, constructorMethod.makeRef(), constructorArgs)
//            )
//        )
//
//        targetMethods.forEach {
//            val targetMethodArgs = Collections.nCopies(it.parameterCount, NullConstant.v())
//            units.add(
//                jimple.newInvokeStmt(
//                    jimple.newVirtualInvokeExpr(allocatedTargetObj, it.makeRef(), targetMethodArgs)
//                )
//            )
//        }

        units.add(jimple.newReturnVoidStmt())

        return mainMethod
    }

    private fun generateInstance(targetClass: SootClass): LocalsAndUnits {
        val jimple = Jimple.v()
        val instanceName = "dummy${targetClass.shortName}Obj"  // instance obj name, e.g., dummyExampleObj
        val allocatedTargetObj = jimple.newLocal(instanceName, targetClass.type)
        val assignStmt = jimple.newAssignStmt(allocatedTargetObj, jimple.newNewExpr(targetClass.type))

        val constructorMethod = getConstructor(targetClass) ?: return LocalsAndUnits(listOf(), listOf())

        val constructorArgs = Collections.nCopies(constructorMethod.parameterCount, NullConstant.v())
        val constructorInvokeStmt = jimple.newInvokeStmt(
                jimple.newSpecialInvokeExpr(
                    allocatedTargetObj, constructorMethod.makeRef(), constructorArgs
                )
            )

        return LocalsAndUnits(
            listOf(allocatedTargetObj),
            listOf(assignStmt, constructorInvokeStmt)
        )
    }

    private fun generateMethodCallStmt(targetMethod: SootMethod, instanceObj: Local): LocalsAndUnits {
        val args = Collections.nCopies(targetMethod.parameterCount, NullConstant.v())
        val invokeStmt = Jimple.v().newInvokeStmt(
                Jimple.v().newVirtualInvokeExpr(
                    instanceObj, targetMethod.makeRef(), args
                )
            )

        return LocalsAndUnits(
            listOf(),
            listOf(invokeStmt)
        )
    }
}
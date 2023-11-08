package org.kotsuite.soot

import org.apache.logging.log4j.LogManager
import org.kotsuite.CommonClassConstants
import org.kotsuite.ValueOfConstants.getValueOfSig
import org.kotsuite.soot.extensions.getConstructor
import org.kotsuite.utils.IDUtils
import soot.*
import soot.Value
import soot.jimple.*
import java.util.*

object SootUtils {

    private val log = LogManager.getLogger()

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
            Scene.v().loadClassAndSupport(CommonClassConstants.object_class_name)

            val mainClass = SootClass(mainClassName, Modifier.PUBLIC)
            mainClass.superclass = Scene.v().getSootClass(CommonClassConstants.object_class_name)
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
     * Create main method which calls the target methods
     *
     * @param targetMethods methods that needs to be called
     * @return main method
     */
    private fun createMainMethod(targetMethods: List<SootMethod>): SootMethod {
        val jimple = Jimple.v()

        val argsParameterType = ArrayType.v(RefType.v(CommonClassConstants.string_class_name), 1)
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
                val instanceLocals = generateInstance(jimpleBody, targetMethod.declaringClass)

                alreadyInstancedClasses.add(targetClassFullName)

                if (instanceLocals != null) {
                    instances.add(instanceLocals)
                }
            }

            val instance = instances.first { instance ->
                instance.type == targetMethod.declaringClass.type
            }

            generateMethodCallStmt(jimpleBody, targetMethod, instance)
        }

        units.add(jimple.newReturnVoidStmt())

        return mainMethod
    }

    /**
     * Generate instance
     *
     * @param body
     * @param targetClass
     * @return instance local
     */
    private fun generateInstance(body: Body, targetClass: SootClass): Local? {
        val jimple = Jimple.v()
        val instanceName = "dummy${targetClass.shortName}Obj"  // instance obj name, e.g., dummyExampleObj
        val allocatedTargetObj = jimple.newLocal(instanceName, targetClass.type)
        val assignStmt = jimple.newAssignStmt(allocatedTargetObj, jimple.newNewExpr(targetClass.type))

        val constructorMethod = targetClass.getConstructor() ?: return null

        val constructorArgs = Collections.nCopies(constructorMethod.parameterCount, NullConstant.v())
        val constructorInvokeStmt = jimple.newInvokeStmt(
                jimple.newSpecialInvokeExpr(
                    allocatedTargetObj, constructorMethod.makeRef(), constructorArgs
                )
            )

        body.locals.addAll(listOf(allocatedTargetObj))
        body.units.addAll(listOf(assignStmt, constructorInvokeStmt))

        return allocatedTargetObj
    }

    private fun generateMethodCallStmt(body: Body, targetMethod: SootMethod, instanceObj: Local) {
        val args = Collections.nCopies(targetMethod.parameterCount, NullConstant.v())
        val invokeStmt = Jimple.v().newInvokeStmt(
                Jimple.v().newVirtualInvokeExpr(
                    instanceObj, targetMethod.makeRef(), args
                )
            )

        body.units.add(invokeStmt)
    }

    fun constantToObject(body: Body, value: Value): Value {
        if (value is Constant && value.type is PrimType) {
            val constantType = value.type as PrimType
            val valueOfMethodSig = constantType.getValueOfSig()
            val valueOfMethod = Scene.v().getMethod(valueOfMethodSig)

            val local = Jimple.v().newLocal("valueOfLocal${IDUtils.getId()}", valueOfMethod.returnType)
            val castStmt = Jimple.v().newAssignStmt(
                local,
                Jimple.v().newStaticInvokeExpr(
                    valueOfMethod.makeRef(),
                    value,
                )
            )

            body.locals.add(local)
            body.units.add(castStmt)

            return local
        }

        return value
    }

}
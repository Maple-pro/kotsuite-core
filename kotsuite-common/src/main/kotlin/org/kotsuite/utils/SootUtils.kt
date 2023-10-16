package org.kotsuite.utils

import org.apache.logging.log4j.LogManager
import soot.*
import soot.jimple.Jimple
import soot.jimple.NullConstant
import soot.tagkit.AnnotationClassElem
import soot.tagkit.AnnotationConstants
import soot.tagkit.AnnotationTag
import soot.tagkit.VisibilityAnnotationTag
import java.util.*

object SootUtils {

    private val log = LogManager.getLogger()

    /**
     * Get local variable by variable name in a method
     *
     * @param sootMethod
     * @param localName
     * @return
     */
    @Throws(Exception::class)
    fun SootMethod.getLocalByName(localName: String): Local {
        val body = this.activeBody
        var local: Local? = null
        for (l in body.locals) {
            if (l.name == localName) {
                local = l
                break
            }
        }

        if (local == null) {
            log.error("Can't get local: $localName")
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
    fun SootClass.getConstructor(): SootMethod? {
        return try {
//            sootClass.getMethod("void <init>()")
            this.getMethodByName("<init>")
        } catch (ex: RuntimeException) {
            if (this.methods.none { it.name == "<init>" }) {
                return null
            }
            this.methods.first { it.name == "<init>" }
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

    fun generateTestAnnotation(): VisibilityAnnotationTag {
        val defaultAnnotationTag = VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE)
        val junitTestAnnotation = AnnotationTag("Lorg/junit/Test;")
        defaultAnnotationTag.addAnnotation(junitTestAnnotation)

        return defaultAnnotationTag
    }

    fun generateRunWithMockitoAnnotation(): VisibilityAnnotationTag {
        return generateRunWithAnnotation("Lorg/mockito/junit/MockitoJUnitRunner;")
    }

    fun generateRunWithRobolectricAnnotation(): VisibilityAnnotationTag {
        return generateRunWithAnnotation("Lorg/robolectric/RobolectricTestRunner;")
    }

    private fun generateRunWithAnnotation(elemDesc: String): VisibilityAnnotationTag {
        val defaultAnnotationTag = VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE)
        val mockitoAnnotationElem = AnnotationClassElem(elemDesc, 'c', "value")
        val runWithAnnotation = AnnotationTag("Lorg/junit/runner/RunWith;", listOf(mockitoAnnotationElem))
        defaultAnnotationTag.addAnnotation(runWithAnnotation)

        return defaultAnnotationTag
    }

}
package org.kotsuite.analysis

import org.slf4j.LoggerFactory
import soot.*
import soot.jimple.Jimple
import soot.jimple.NullConstant
import soot.options.Options
import java.io.File
import java.util.Collections
import kotlin.streams.toList

/**
 * Each analyzer corresponds to a project to be tested.
 */
object Analyzer {
    private val log = LoggerFactory.getLogger(this.javaClass)

    var exampleProjectDir = ""
    var classesOrPackagesToAnalyze = listOf<String>()

    var classes = listOf<SootClass>()  // All classes under `classesOrPackagesToAnalyze`
    val sootScene = Scene.v()

    /**
     * Analyze classes in the data directory, and transform them into jimple.
     */
    fun analyze(): Boolean {
        log.info("Analysis project: project($exampleProjectDir)")

        val res = setupSoot()
        if (!res) {
            return false
        }
        Scene.v().loadNecessaryClasses()

        classes = Scene.v().classes.stream().filter{ classesOrPackagesToAnalyze.contains(it.packageName) }.toList()

        return true
    }

    /**
     * Analyze method
     *
     * @param methodSig the method signature to be analyzed
     * @return the corresponding soot method
     */
    fun analyzeMethod(methodSig: MethodSignature): SootMethod {
        log.info("Analyze method: " +
                "project($exampleProjectDir), class($classesOrPackagesToAnalyze), method name(${methodSig.methodName})")

        setupSoot()
        Scene.v().loadNecessaryClasses()

        val sootMethod = createTestTarget(methodSig)
        runSoot()

        return sootMethod
    }

    /**
     * Set up soot parameters
     */
    private fun setupSoot(): Boolean {
        log.info("Setup Soot: class($classesOrPackagesToAnalyze), project($exampleProjectDir")

        val classesDir = "${exampleProjectDir}/app/build/tmp/kotlin-classes/debug/"
        val file = File(classesDir)
        if (!file.isDirectory) {
            log.error("Classes Directory not exists: $classesDir")
            return false
        }

        G.reset()
        with(Options.v()) {
            set_whole_program(true)
            set_output_format(Options.output_format_jimple)
            set_allow_phantom_refs(true)
            set_no_bodies_for_excluded(true)
            set_exclude(getExcludes())
            set_include(ArrayList(classesOrPackagesToAnalyze))
            set_process_dir(listOf(classesDir))
            set_validate(true)
        }
        return true
    }

    /**
     * Run soot
     */
    private fun runSoot() {
        log.info("Run Soot")
        PackManager.v().runPacks()
    }

    /**
     * Get exclude packages or classes
     */
    private fun getExcludes(): ArrayList<String> {
        val excludeList = ArrayList<String>()

        return excludeList
    }

    /**
     * Create soot method of the target method.
     *
     * Create a dummy main method which calls the target method.
     * Then set the dummy main class as the application class,
     * and set the dummy main method as the entry point of soot.
     *
     * @param methodSig method signature of the target method
     * @return the soot method of the target method
     */
    private fun createTestTarget(methodSig: MethodSignature): SootMethod {
        val sootTestMethod = getMethodForSig(methodSig)
        val targetClass = makeDummyClass(sootTestMethod)
        Scene.v().addClass(targetClass)
        targetClass.setApplicationClass()
        Scene.v().entryPoints = listOf(targetClass.getMethodByName("main"))

        return sootTestMethod
    }

    /**
     * Create a dummy main class and dummy main method which calls the target method.
     *
     * @param sootTestMethod the target soot method
     * @return the dummy main class
     */
    private fun makeDummyClass(sootTestMethod: SootMethod): SootClass {
        val sootClass = SootClass("dummyClass")
        val argsParameterType = ArrayType.v(RefType.v("java.lang.String"), 1)
        val mainMethod = SootMethod("main", listOf(argsParameterType), VoidType.v(),
            Modifier.PUBLIC or Modifier.STATIC)
        sootClass.addMethod(mainMethod)

        val jimple = Jimple.v()
        val jimpleBody = jimple.newBody(mainMethod)
        mainMethod.activeBody = jimpleBody
        val locals = jimpleBody.locals
        val units = jimpleBody.units

        val argsParameter = jimple.newLocal("args", argsParameterType)
        locals.add(argsParameter)
        units.add(jimple.newIdentityStmt(argsParameter, jimple.newParameterRef(argsParameterType, 0)))

        val testCaseType = RefType.v(sootTestMethod.declaringClass)
        val allocatedTestObj = jimple.newLocal("dummyObj", testCaseType)
        locals.add(allocatedTestObj)
        units.add(jimple.newAssignStmt(allocatedTestObj, jimple.newNewExpr(testCaseType)))

        val method: SootMethod = try {
            testCaseType.sootClass.getMethod("void <init>()")
        } catch (ex: RuntimeException) {
            testCaseType.sootClass.getMethodByName("<init>")
        }

        val constructorArgs = Collections.nCopies(method.parameterCount, NullConstant.v())
        units.add(jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(allocatedTestObj, method.makeRef(), constructorArgs)))

        val args = Collections.nCopies(sootTestMethod.parameterCount, NullConstant.v())
        units.add(jimple.newInvokeStmt(jimple.newVirtualInvokeExpr(allocatedTestObj, sootTestMethod.makeRef(), args)))
        units.add(jimple.newReturnVoidStmt())

        return sootClass
    }

    /**
     * Get the soot method for the given method signature
     *
     * @param methodSig the given method signature
     * @return the soot method
     */
    private fun getMethodForSig(methodSig: MethodSignature): SootMethod {
        val sootClass = Scene.v().getSootClass(methodSig.clazz)
        return sootClass.getMethodByName(methodSig.methodName)
    }

}
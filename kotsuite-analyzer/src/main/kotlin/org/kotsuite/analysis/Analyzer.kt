package org.kotsuite.analysis

import org.slf4j.LoggerFactory
import soot.*
import soot.jimple.Jimple
import soot.jimple.NullConstant
import soot.options.Options
import java.io.File
import java.util.Collections

/**
 * Each analyzer corresponds to a project to be tested.
 */
object Analyzer {
    private val log = LoggerFactory.getLogger(this.javaClass)

    var projectPath = ""
    var includeRules = listOf<String>()

    var classes = listOf<SootClass>()  // All classes under `classesOrPackagesToAnalyze`

    /**
     * Analyze classes in the data directory, and transform them into jimple.
     */
    fun analyze(classPath: String, dependencyCLassPaths: List<String>): Boolean {
        log.info("Analysis project: project($projectPath)")

        log.info("Start analyze project")

        val res = setupSoot(classPath, dependencyCLassPaths)
        if (!res) {
            return false
        }
        Scene.v().loadNecessaryClasses()

        log.info("Finish analyze project")

        classes = Scene.v().classes.filter{
            sootClass ->
            includeRules.any { sootClass.name.startsWith(it) }
        }

        return true
    }

    /**
     * Analyze method
     *
     * @param methodSig the method signature to be analyzed
     * @return the corresponding soot method
     */
//    fun analyzeMethod(methodSig: MethodSignature): SootMethod {
//        log.info("Analyze method: " +
//                "project($projectPath), class($includeRules), method name(${methodSig.methodName})")
//
//        setupSoot()
//        Scene.v().loadNecessaryClasses()
//
//        val sootMethod = createTestTarget(methodSig)
//        runSoot()
//
//        return sootMethod
//    }

    /**
     * Set up soot parameters
     */
    private fun setupSoot(classPath: String, dependencyClassPaths: List<String>): Boolean {
        log.info("Setup Soot: class($includeRules), project($projectPath")

        val file = File(classPath)
        if (!file.isDirectory) {
            log.error("Classes Directory not exists: $classPath")
            return false
        }

        G.reset()
        with(Options.v()) {
            set_prepend_classpath(true)
            set_whole_program(true)
            set_output_format(Options.output_format_jimple)
            set_allow_phantom_refs(true)
            set_no_bodies_for_excluded(true)
            set_exclude(getExcludes())
            set_include(ArrayList(includeRules))
            set_process_dir(listOf(classPath) + dependencyClassPaths)
//            set_process_dir(listOf(classPath))
//            set_soot_classpath(dependencyClassPathsList.joinToString(":"))
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
     * Example main method:
     *     public static void main(java.lang.String[])
     *     {
     *         com.example.myapplication.FirstFragment dummyObj;
     *         java.lang.String[] args;
     *
     *         args := @parameter0: java.lang.String[];
     *
     *         dummyObj = new com.example.myapplication.FirstFragment;
     *
     *         specialinvoke dummyObj.<com.example.myapplication.FirstFragment: void <init>()>();
     *
     *         virtualinvoke dummyObj.<com.example.myapplication.FirstFragment: void onDestroyView()>();
     *
     *         return;
     *     }
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
        units.add(
            jimple.newInvokeStmt(
                jimple.newSpecialInvokeExpr(allocatedTestObj, method.makeRef(), constructorArgs)
            )
        )

        val args = Collections.nCopies(sootTestMethod.parameterCount, NullConstant.v())
        units.add(
            jimple.newInvokeStmt(
                jimple.newVirtualInvokeExpr(allocatedTestObj, sootTestMethod.makeRef(), args)
            )
        )
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
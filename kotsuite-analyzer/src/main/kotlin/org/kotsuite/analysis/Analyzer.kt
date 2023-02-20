package org.kotsuite.analysis

import org.slf4j.LoggerFactory
import soot.*
import soot.jimple.Jimple
import soot.jimple.JimpleBody
import soot.jimple.NullConstant
import soot.options.Options
import java.io.File
import java.util.Collections

/**
 * Each analyzer corresponds to a project to be tested.
 */
class Analyzer(private val exampleProjectDir: String, private val classesOrPackagesToAnalyze: Collection<String>) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val classesDir = "${exampleProjectDir}/app/build/tmp/kotlin-classes/debug/"
    private var sootClasses: Array<SootClass> = arrayOf()
    private var jimpleClasses: Array<Jimple> = arrayOf()

    companion object {
        /**
         * Method Signature: <Analyzer: Boolean analyze()>
         */
        private fun methodSigFromComponents(clazz: String, subSig: String): String {
            return String.format("<%s: %s>", clazz, subSig)
        }

        private fun methodSigFromComponents(
            clazz: String, returnType: String, methodName: String, params: Array<String>): String {
            return methodSigFromComponents(
                clazz, String.format("%s %s(%s)", returnType, methodName, params.joinToString(",")))
        }
    }

    /**
     * Analysis classes in the data directory, and transform them into jimple.
     */
    fun analyze(): Boolean {
        // TODO
        log.info("Analysis: $exampleProjectDir")

        val file = File(classesDir)
        if (!file.isDirectory) {
            log.error("Classes Directory not exists.")
            return false
        }


//        prepareTarget()

        return true
    }

    fun analyzeMethod(methodSig: MethodSignature): SootMethod {
        log.info("Prepare Target: ${methodSig.methodName}, $classesOrPackagesToAnalyze")

        setupSoot()
        Scene.v().loadNecessaryClasses()

        val sootMethod = createTestTarget(methodSig)
        runSoot()

        log.info(sootMethod.retrieveActiveBody().toString())

        return sootMethod
    }

    private fun setupSoot() {
        log.info("Setup Soot: $classesOrPackagesToAnalyze, $exampleProjectDir")

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
    }

    private fun runSoot() {
        log.info("Run Soot")
        PackManager.v().runPacks()
    }

    private fun getExcludes(): ArrayList<String> {
        val excludeList = ArrayList<String>()

        return excludeList
    }

    private fun createTestTarget(methodSig: MethodSignature): SootMethod {
        val sootTestMethod = getMethodForSig(methodSig)
        val targetClass = makeDummyClass(sootTestMethod)
        Scene.v().addClass(targetClass)
        targetClass.setApplicationClass()
        Scene.v().entryPoints = listOf(targetClass.getMethodByName("main"))

        return sootTestMethod
    }

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

    private fun getMethodForSig(methodSig: MethodSignature): SootMethod {
        val sootClass = Scene.v().getSootClass(methodSig.clazz)
        return sootClass.getMethodByName(methodSig.methodName)
    }

    // The following are deprecated functions

    /**
     * Set up soot parameters.
     */
    fun setupSoot(className: String, methodNames: Array<String>) {
        log.info("Setup soot: $className, $methodNames")

        G.reset()
        with(Options.v()) {
            set_prepend_classpath(true)
            set_allow_phantom_elms(true)
            set_soot_classpath(exampleProjectDir)
            set_output_format(Options.output_format_jimple)
        }
        val sootClass = Scene.v().loadClassAndSupport(className)
        sootClass.setApplicationClass()
        Scene.v().loadNecessaryClasses()
    }

    /**
     * Run soot.
     */
    fun runSoot(className: String, methodNames: Array<String>) {
        log.info("Run Soot: $className, $methodNames")

        val mainClass = Scene.v().getSootClass(className)
        for (methodName in methodNames) {
            val sootMethod = mainClass.getMethodByName(methodName)
            val jimpleBody = sootMethod.retrieveActiveBody() as JimpleBody

            println("Transforming $methodName ...")

            println(jimpleBody)
        }
    }
}
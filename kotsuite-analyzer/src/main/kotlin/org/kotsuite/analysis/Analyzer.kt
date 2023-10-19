package org.kotsuite.analysis

import com.google.gson.GsonBuilder
import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import org.kotsuite.utils.soot.SootUtils
import soot.*
import soot.options.Options
import java.io.File

/**
 * Each analyzer corresponds to a project to be tested.
 */
object Analyzer {
    private val log = LogManager.getLogger()

    var projectPath = ""
    var includeRules = listOf<String>()

    var classes = listOf<SootClass>()  // All classes under `classesOrPackagesToAnalyze`

    /**
     * Analyze classes in the data directory, and transform them into jimple.
     */
    fun analyze(): Boolean {
        log.info("Analysis project: project($projectPath)")

        log.info("Start analyze project")

        val res = setupSoot(Configs.classesFilePath, Configs.dependencyClassPaths)
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
     * Set up soot parameters
     */
    private fun setupSoot(classPath: List<String>, dependencyClassPaths: List<String>): Boolean {
        log.info("Setup Soot: class($includeRules), project($projectPath)")

        val inputClassPaths = mutableListOf<String>() // 待测类文件路径
        for (path in classPath) {
            val file = File(path)
            if (file.exists() && file.isDirectory) {
                inputClassPaths.add(path)
            }
        }

        val testFrameworks = Dependency.getTestFramework() // 测试框架依赖 jar 包路径
        val testDependencies = Dependency.getTestDependencies() // 测试框架依赖

        val sootProcessDir = inputClassPaths + testFrameworks
        val sootClasspath = (dependencyClassPaths + testDependencies).joinToString(File.pathSeparator)

        val gson = GsonBuilder().setPrettyPrinting().create()
        log.debug("Test frameworks: \n${gson.toJson(testFrameworks)}")

        G.reset()
        with(Options.v()) {
            set_prepend_classpath(true)
            set_whole_program(false)
            set_output_format(Options.output_format_jimple)
            set_allow_phantom_refs(true)
            set_no_bodies_for_excluded(true)
            set_exclude(getExcludes())
            set_include(ArrayList(includeRules))
            set_process_dir(sootProcessDir)
            set_soot_classpath(sootClasspath)
            set_validate(true)
//            set_app(true)
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
     *
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
        return SootUtils.generateMainClass("dummyClass", listOf(sootTestMethod))
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
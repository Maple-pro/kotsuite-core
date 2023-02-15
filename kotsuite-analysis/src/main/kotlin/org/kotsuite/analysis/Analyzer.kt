package org.kotsuite.analysis

import org.slf4j.LoggerFactory
import soot.G
import soot.Scene
import soot.SootClass
import soot.jimple.Jimple
import soot.jimple.JimpleBody
import soot.options.Options
import java.io.File

/**
 * Each analyzer corresponds to a project to be tested.
 */
class Analyzer(private val dataDir: String) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val classesDir = "${dataDir}/app/build/tmp/kotlin-classes/debug/"
    private var sootClasses: Array<SootClass> = arrayOf()
    private var jimpleClasses: Array<Jimple> = arrayOf()

    /**
     * Analysis classes in the data directory, and transform them into jimple.
     */
    fun analysis(): Boolean {
        log.info("Analysis: $dataDir")

        val file = File(classesDir)
        if (!file.isDirectory) {
            log.error("Classes Directory not exists.")
            return false
        }



        return true
    }

    /**
     * Set up soot parameters.
     */
    fun setupSoot(className: String, methodNames: Array<String>) {
        log.info("Setup soot: $className, $methodNames")

        G.reset()
        with(Options.v()) {
            set_prepend_classpath(true)
            set_allow_phantom_elms(true)
            set_soot_classpath(dataDir)
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
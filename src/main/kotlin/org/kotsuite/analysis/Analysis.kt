package org.kotsuite.analysis

import soot.G
import soot.Scene
import soot.jimple.JimpleBody
import soot.options.Options

class Analysis(
    private val sourceDirectory: String,
    private val className: String,
    private val methodNames: List<String>) {

    fun setupSoot() {
        G.reset()
        with(Options.v()) {
            set_prepend_classpath(true)
            set_allow_phantom_elms(true)
            set_soot_classpath(sourceDirectory)
            set_output_format(Options.output_format_jimple)
        }
        val sootClass = Scene.v().loadClassAndSupport(className)
        sootClass.setApplicationClass()
        Scene.v().loadNecessaryClasses()
    }

    fun runSoot() {
        val mainClass = Scene.v().getSootClass(className)
        for (methodName in methodNames) {
            val sootMethod = mainClass.getMethodByName(methodName)
            val jimpleBody = sootMethod.retrieveActiveBody() as JimpleBody

            println("Transforming $methodName ...")

            println(jimpleBody)
        }
    }
}
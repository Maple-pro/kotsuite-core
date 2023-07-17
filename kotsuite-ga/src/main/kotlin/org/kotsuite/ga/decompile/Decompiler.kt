package org.kotsuite.ga.decompile

import org.kotsuite.ga.Configs
import org.kotsuite.ga.utils.LoggerUtils
import org.slf4j.LoggerFactory

/**
 * Decompile .class file to java file and kotlin file
 */
object Decompiler {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun decompileJasminToJava(classesPath: String, javaOutputPath: String) {
        val command = arrayOf(
            "java",
            "-jar", Configs.decompilerPath,
            classesPath,
            javaOutputPath,
        )

        try {
            val ps = Runtime.getRuntime().exec(command)
            LoggerUtils.logCommandOutput(log, ps)
            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }
}
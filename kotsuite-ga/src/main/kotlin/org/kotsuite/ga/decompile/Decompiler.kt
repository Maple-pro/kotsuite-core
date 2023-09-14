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
            "-das=1", // decompiler assertions
            "-rbr=0", // hide bridge methods
            "-rsy=0", // hide synthetic class members
            "-ren=0", // rename
            "-bto=0", // interpret int 1 as boolean true
            "-fdi=1", // de-inline finally structures
//            "-log=TRACE", // logging level
            classesPath,
            javaOutputPath,
        )

        try {
            val ps = Runtime.getRuntime().exec(command)
            LoggerUtils.logCommandOutput(log, ps, Configs.SHOW_DEBUG_LEVEL)
            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }
}
package org.kotsuite.ga.decompile

import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import org.kotsuite.utils.LoggerUtils

/**
 * Decompile .class file to java file and kotlin file
 */
object Decompiler {

    private val log = LogManager.getLogger()

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
            LoggerUtils.logCommandOutput(log, ps)
            ps.waitFor()
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }
}
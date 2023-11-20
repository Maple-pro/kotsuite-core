package org.kotsuite.ga.decompile

import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import org.kotsuite.exception.DecompileException
import org.kotsuite.utils.execCommand
import org.kotsuite.utils.getError
import org.kotsuite.utils.getOutput
import org.kotsuite.utils.logCommandOutput
import java.io.IOException

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
            val res = execCommand(command)
            val psInput = res.first
            val psError = res.second
            log.logCommandOutput(psInput, psError)
        } catch (e: IOException) {
            log.error("Failed to decompile jasmin to java")
            throw DecompileException("Failed to decompile jasmin to java", e)
        }
    }
}
package org.kotsuite.ga.chromosome.printer

import soot.SootClass
import soot.SourceLocator
import soot.baf.BafASMBackend
import soot.options.Options
import java.io.FileOutputStream

class JasminPrinter(private val outputFileDir: String) {

    fun printJasminFile(sootClass: SootClass) {
        val javaVersion = Options.v().java_version()
        val fileName = SourceLocator.v().getFileNameFor(sootClass, Options.output_format_class)
        val finalDir = "$outputFileDir/$fileName"

        // use outdated backend
//        val streamOut = JasminOutputStream(FileOutputStream(finalDir))
//        val writeOut = PrintWriter(OutputStreamWriter(streamOut))
//        val jasminClass = JasminClass(sootClass)
//        jasminClass.print(writeOut)
//        writeOut.flush()
//        streamOut.close()

        // use asm backend
        val streamOut = FileOutputStream(finalDir)
        val backend = BafASMBackend(sootClass, javaVersion)
        backend.generateClassFile(streamOut)
        streamOut.close()
    }

}
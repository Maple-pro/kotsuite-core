package org.kotsuite.ga.chromosome.generator.jimple

import soot.SootClass
import soot.SourceLocator
import soot.jimple.JasminClass
import soot.options.Options
import soot.util.JasminOutputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter

class JasminPrinter(private val outputFileDir: String) {
    fun printJasminFile(sootClass: SootClass) {
        val fileName = SourceLocator.v().getFileNameFor(sootClass, Options.output_format_class)
        val finalDir = "$outputFileDir/$fileName"
//        val finalDir = "$outputFileDir/${sootClass.shortName}.class"
        val streamOut = JasminOutputStream(FileOutputStream(finalDir))
        val writeOut = PrintWriter(OutputStreamWriter(streamOut))
        val jasminClass = JasminClass(sootClass)
        jasminClass.print(writeOut)
        writeOut.flush()
        streamOut.close()
    }
}
package org.kotsuite.ga.chromosome.generator

import soot.SootClass
import soot.SourceLocator
import soot.jimple.JasminClass
import soot.options.Options
import soot.util.JasminOutputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter

class JasminPrinter(outputFileDir: String) {
    fun printJasminFile(sootClass: SootClass) {
        val fileName = SourceLocator.v().getFileNameFor(sootClass, Options.output_format_class)
        println("111 $fileName")
        val streamOut = JasminOutputStream(FileOutputStream(fileName))
        val writeOut = PrintWriter(OutputStreamWriter(streamOut))
        val jasminClass = JasminClass(sootClass)
        jasminClass.print(writeOut)
        writeOut.flush()
        streamOut.close()
    }
}
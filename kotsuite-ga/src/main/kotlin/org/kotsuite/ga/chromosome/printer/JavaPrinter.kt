package org.kotsuite.ga.chromosome.printer

import soot.SootClass
import soot.SourceLocator
import soot.options.Options
import java.io.FileOutputStream
import java.io.PrintWriter

// Use decompiler to output java file (JUnit tests)
class JavaPrinter(private val outputFileDir: String) {

    fun printJavaFile(sootClass: SootClass) {
        val outputStream = FileOutputStream("$outputFileDir/${sootClass.shortName}.java")
        val writer = PrintWriter(outputStream)
    }
}
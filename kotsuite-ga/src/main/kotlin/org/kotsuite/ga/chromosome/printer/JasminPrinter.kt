package org.kotsuite.ga.chromosome.printer

import org.kotsuite.ga.Configs
import soot.SootClass
import soot.SourceLocator
import soot.baf.BafASMBackend
import soot.options.Options
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

object JasminPrinter {

    private val outputFileDir = Configs.modulePath

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

        val lastIndex = finalDir.lastIndexOf("/")
        val directoryPath = finalDir.substring(0, lastIndex + 1)
        Files.createDirectories(Paths.get(directoryPath))

        // use asm backend
        val streamOut = FileOutputStream(finalDir)
        val backend = BafASMBackend(sootClass, javaVersion)
        backend.generateClassFile(streamOut)
        streamOut.close()
    }

}
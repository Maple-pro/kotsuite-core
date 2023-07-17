package org.kotsuite.ga.chromosome.printer

import org.kotsuite.ga.Configs
import soot.SootClass
import soot.SourceLocator
import soot.baf.BafASMBackend
import soot.jimple.JasminClass
import soot.options.Options
import soot.util.JasminOutputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths

object JasminPrinter {

    private val outputFileDir = Configs.sootOutputPath

    /**
     * Print jasmin file
     *
     * @param sootClass     the soot class needs to be printed
     * @param outputFileDir the output direction path, it will print to `$outputFileDir/sootOutput`
     * @param asmBackend    whether to use asm backend or outdated backend
     */
    fun printJasminFile(
        sootClass: SootClass,
        outputFileDir: String = this.outputFileDir,
        asmBackend: Boolean = true,
    ) {
        val javaVersion = Options.v().java_version()

        // `getFileNameFor()` will return "sootOutput/com/example/myapplication/.../.class", so we need to remove the "sootOutput"
        val fileName = SourceLocator.v().getFileNameFor(sootClass, Options.output_format_class).substringAfter('/')

        val finalDir = "$outputFileDir/$fileName"

        val lastIndex = finalDir.lastIndexOf("/")
        val directoryPath = finalDir.substring(0, lastIndex + 1)
        Files.createDirectories(Paths.get(directoryPath))

        if (!asmBackend) {
            // use outdated backend
            val streamOut = JasminOutputStream(FileOutputStream(finalDir))
            val writeOut = PrintWriter(OutputStreamWriter(streamOut))
            val jasminClass = JasminClass(sootClass)
            jasminClass.print(writeOut)
            writeOut.flush()
            streamOut.close()
        } else {
            // use asm backend
            val streamOut = FileOutputStream(finalDir)
            val backend = BafASMBackend(sootClass, javaVersion)
            backend.generateClassFile(streamOut)
            streamOut.close()
        }
    }

}
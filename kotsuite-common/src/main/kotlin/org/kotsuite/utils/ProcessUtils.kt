package org.kotsuite.utils

import org.apache.logging.log4j.LogManager
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

private val log = LogManager.getLogger()

fun execCommand(cmdArray: Array<String>): Pair<List<String>, List<String>> {
    val ps = Runtime.getRuntime().exec(cmdArray)

    // Create two threads to read the output and error streams of the process concurrently
    val psInput = mutableListOf<String>()
    val psError = mutableListOf<String>()
    val inputStreamThread = ps.collectInputStream(psInput)
    val errorStreamThread = ps.collectErrorStream(psError)

    // Wait for the process to finish
    val exitCode = ps.waitFor(3, TimeUnit.MINUTES)

    // Wait for the two threads to finish
    inputStreamThread.join()
    errorStreamThread.join()

    if (!exitCode) {
        log.error("Process timeout")
    }
    ps.destroy()

    return Pair(psInput, psError)
}

fun Process.getOutput(): List<String> {
    val stdInput = BufferedReader(InputStreamReader(this.inputStream))
    return stdInput.readLines()
}

fun Process.getError(): List<String> {
    val stdOutput = BufferedReader(InputStreamReader(this.errorStream))
    return stdOutput.readLines()
}

fun Process.collectInputStream(result: MutableList<String>): Thread {
    val inputStreamThread = Thread {
        readStream(this.inputStream) {
            result.add(it)
        }
    }
    inputStreamThread.start()

    return inputStreamThread
}

fun Process.collectErrorStream(result: MutableList<String>): Thread {
    val errorStreamThread = Thread {
        readStream(this.errorStream) {
            result.add(it)
        }
    }
    errorStreamThread.start()

    return errorStreamThread
}

fun readStream(inputStream: InputStream, lineHandler: (String) -> Unit) {
    BufferedReader(InputStreamReader(inputStream)).use { reader ->
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            lineHandler(line!!)
        }
    }
}

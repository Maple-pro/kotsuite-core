package org.kotsuite.utils

import java.io.BufferedReader
import java.io.InputStreamReader

fun Process.getOutput(): List<String> {
    val stdInput = BufferedReader(InputStreamReader(this.inputStream))
    return stdInput.readLines()
}

fun Process.getError(): List<String> {
    val stdOutput = BufferedReader(InputStreamReader(this.errorStream))
    return stdOutput.readLines()
}
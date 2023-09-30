package org.kotsuite.ga.chromosome

import org.kotsuite.ga.chromosome.action.Action
import org.kotsuite.ga.chromosome.value.Value
import org.kotsuite.ga.coverage.fitness.Fitness
import soot.SootMethod
import java.io.BufferedReader
import java.io.InputStreamReader

class TestCase(
    val testCaseName: String,
    val targetMethod: SootMethod,
    val round: Int
): Element() {
    val actions = mutableListOf<Action>()
    val values = mutableListOf<Value>()
    var assertType: String? = null
    var assertValue: String? = null  // assert value
    var fitness: Fitness? = null

    fun generateAssertByProcess(process: Process) {
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        val stringBuilder = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line).append("\n")
        }

        /**
         * Output string, e.g.,
         * <assert>
         *     <type>int</type>
         *     <value>
         *         32
         *     </value>
         * </assert>
         */
        val outputString = stringBuilder.toString()

        val startTag = "<assert>"
        val endTag = "</assert>"

        val assertOutput = outputString
            .substringAfter(startTag).substringBefore(endTag)
            .replace("\n", "")

        val typeRegex = "<type>(.*?)</type>".toRegex()
        val typeMatchResult = typeRegex.find(assertOutput)
        val type = typeMatchResult?.groupValues?.get(1)

        val valueRegex = "<value>(.*?)</value>".toRegex()
        val valueMatchResult = valueRegex.find(assertOutput)
        val value = valueMatchResult?.groupValues?.get(1)

        this.assertType = type
        this.assertValue = value
    }

    fun generateAssertByFile(assertFilePath: String) {
        TODO("Not implemented yet")
    }

}
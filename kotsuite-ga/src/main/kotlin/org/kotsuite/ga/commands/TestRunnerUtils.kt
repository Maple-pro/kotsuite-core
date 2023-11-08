package org.kotsuite.ga.commands

object TestRunnerUtils {
    fun getTestResult(psOutput: List<String>): Boolean {
        var testResult = false
        psOutput.forEach {
            if (it == "Test successful: true") {
                testResult = true
            }
        }

        return testResult
    }
}
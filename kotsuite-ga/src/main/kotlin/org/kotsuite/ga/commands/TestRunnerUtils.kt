package org.kotsuite.ga.commands

object TestRunnerUtils {
    fun getTestResult(psOutput: List<String>): TestResult {
        var testResult = TestResult.CRASHED
        psOutput.forEach {
            if (it == "Test successful: true") {
                testResult = TestResult.SUCCESSFUL
            } else if (it == "Test successful: false") {
                testResult = TestResult.FAILED
            }
        }

        return testResult
    }
}
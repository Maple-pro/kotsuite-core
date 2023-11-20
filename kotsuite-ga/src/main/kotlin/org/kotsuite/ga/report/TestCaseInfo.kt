package org.kotsuite.ga.report

import org.kotsuite.ga.commands.TestResult

data class TestCaseInfo(
    val testCaseName: String,
    val testResult: TestResult,
    val coverageInfo: CoverageInfo,
)

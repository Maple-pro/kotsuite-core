package org.kotsuite.ga.report

data class TestCaseInfo(
    val testCaseName: String,
    val testResult: Boolean,
    val coverageInfo: CoverageInfo,
)

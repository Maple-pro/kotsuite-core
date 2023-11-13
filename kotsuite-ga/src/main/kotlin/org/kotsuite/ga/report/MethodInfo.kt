package org.kotsuite.ga.report

data class MethodInfo(
    val methodSig: String,
    val methodType: MethodType,
    val methodReason: MethodReason,
    val coverageInfo: CoverageInfo,
    val testCaseInfos: List<TestCaseInfo>,
)
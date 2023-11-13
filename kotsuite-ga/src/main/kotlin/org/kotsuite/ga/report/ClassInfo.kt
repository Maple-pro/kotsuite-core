package org.kotsuite.ga.report

data class ClassInfo(
    val className: String,
    val classType: ClassType,
    val reason: ClassReason,
    val coverageInfo: CoverageInfo,
    val methodInfos: List<MethodInfo>,
)

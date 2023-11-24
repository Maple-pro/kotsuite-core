package org.kotsuite.ga.overall

import org.kotsuite.ga.report.ClassReason
import org.kotsuite.ga.report.ClassType

data class SimpleClassStatistic(
    val className: String,
    val classType: ClassType,
    val classReason: ClassReason,
    val methods: List<SimpleMethodStatistic>,
)

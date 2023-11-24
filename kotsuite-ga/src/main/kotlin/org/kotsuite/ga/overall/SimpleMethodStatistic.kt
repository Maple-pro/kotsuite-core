package org.kotsuite.ga.overall

import org.kotsuite.ga.report.MethodReason
import org.kotsuite.ga.report.MethodType

data class SimpleMethodStatistic(
    val methodSig: String,
    val methodType: MethodType,
    val methodReason: MethodReason,
)

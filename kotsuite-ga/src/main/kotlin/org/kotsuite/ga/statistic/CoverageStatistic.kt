package org.kotsuite.ga.statistic

import com.google.gson.annotations.SerializedName
import org.kotsuite.ga.report.CoverageInfo

data class CoverageStatistic(
    @SerializedName("行覆盖率")
    val lineCoverage: Double,

    @SerializedName("圈复杂度覆盖率")
    val ccCoverage: Double,
) {
    companion object {
        fun fromCoverageInfo(coverageInfo: CoverageInfo): CoverageStatistic {
            return CoverageStatistic(
                coverageInfo.lineCoverage,
                coverageInfo.ccCoverage,
            )
        }
    }
}

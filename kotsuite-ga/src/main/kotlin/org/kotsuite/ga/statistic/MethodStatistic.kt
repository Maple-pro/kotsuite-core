package org.kotsuite.ga.statistic

import com.google.gson.annotations.SerializedName
import org.kotsuite.ga.report.MethodInfo

data class MethodStatistic(
    @SerializedName("方法签名")
    val methodSig: String,

    @SerializedName("测试用例的覆盖率统计")
    val coverage: CoverageStatistic,

    @SerializedName("所有测试用例的数量")
    val allTestCaseNumber: Int,

    @SerializedName("成功的测试用例的数量")
    val successTestCaseNumber: Int,

    @SerializedName("失败的测试用例的数量")
    val failTestCaseNumber: Int,
) {
    companion object {
        fun fromMethodInfo(methodInfo: MethodInfo): MethodStatistic {
            val methodSig = methodInfo.methodSig
            val coverage = CoverageStatistic.fromCoverageInfo(methodInfo.coverageInfo)
            val allTestCaseNumber = methodInfo.testCaseInfos.size
            val successTestCaseNumber = methodInfo.testCaseInfos.count { it.testResult }
            val failTestCaseNumber = allTestCaseNumber - successTestCaseNumber

            return MethodStatistic(
                methodSig,
                coverage,
                allTestCaseNumber,
                successTestCaseNumber,
                failTestCaseNumber,
            )
        }
    }
}

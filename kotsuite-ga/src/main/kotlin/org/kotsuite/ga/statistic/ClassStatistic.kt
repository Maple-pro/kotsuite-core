package org.kotsuite.ga.statistic

import com.google.gson.annotations.SerializedName
import org.kotsuite.ga.report.ClassInfo

data class ClassStatistic(
    @SerializedName("类名")
    val className: String,

    @SerializedName("覆盖率统计")
    val coverageStatistic: CoverageStatistic,

    @SerializedName("成员方法的数量")
    val allMethodsNumber: Int,

    @SerializedName("成员方法的统计信息")
    val methodsStatistic: MethodsStatistic,
) {
    companion object {
        fun fromClassInfo(classInfo: ClassInfo): ClassStatistic {
            val className = classInfo.className
            val coverageStatistic = CoverageStatistic.fromCoverageInfo(classInfo.coverageInfo)
            val allMethodsNumber = classInfo.methodInfos.size
            val methodsStatistic = MethodsStatistic.fromMethodInfos(classInfo.methodInfos)

            return ClassStatistic(
                className,
                coverageStatistic,
                allMethodsNumber,
                methodsStatistic,
            )
        }
    }
}

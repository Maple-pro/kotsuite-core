package org.kotsuite.ga.statistic

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.kotsuite.ga.report.Report

data class Statistic(
    @SerializedName("所有类的数量")
    val allClassNumber: Int,

    @SerializedName("所有类的统计信息")
    val classesStatistic: ClassesStatistic
) {
    companion object {
        fun fromReport(report: Report): Statistic {
            val allClassNumber = report.classInfos.size
            val classesStatistic = ClassesStatistic.fromClassInfos(report.classInfos)

            return Statistic(allClassNumber, classesStatistic)
        }
    }

    override fun toString(): String {
        val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
        return gson.toJson(this)
    }
}


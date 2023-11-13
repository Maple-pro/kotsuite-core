package org.kotsuite.ga.statistic

import com.google.gson.annotations.SerializedName
import org.kotsuite.ga.report.MethodInfo
import org.kotsuite.ga.report.MethodReason
import org.kotsuite.ga.report.MethodType

data class MethodsStatistic(
    @SerializedName("不需要测试的方法的统计信息")
    val noNeedToTestMethodsStatistic: NoNeedToTestMethodsStatistic,

    @SerializedName("需要但是无法测试的方法的统计信息")
    val needButUnableMethodsStatistic: NeedButUnableMethodsStatistic,

    @SerializedName("可以测试的方法的统计信息")
    val enableMethodsStatistic: EnableMethodsStatistic,
) {
    companion object {
        fun fromMethodInfos(methodInfos: List<MethodInfo>): MethodsStatistic {
            val noNeedToTestMethods = methodInfos.filter { it.methodType == MethodType.NO_NEED_TO_TEST }
            val noNeedToTestMethodsStatistic = NoNeedToTestMethodsStatistic.fromMethodInfos(noNeedToTestMethods)

            val needButUnableMethods = methodInfos.filter { it.methodType == MethodType.NEED_BUT_UNABLE }
            val needButUnableMethodsStatistic = NeedButUnableMethodsStatistic.fromMethodInfos(needButUnableMethods)

            val enableMethods = methodInfos.filter { it.methodType == MethodType.ENABLE }
            val enableMethodsStatistic = EnableMethodsStatistic.fromMethodInfos(enableMethods)

            return MethodsStatistic(
                noNeedToTestMethodsStatistic,
                needButUnableMethodsStatistic,
                enableMethodsStatistic,
            )
        }
    }
}

data class NoNeedToTestMethodsStatistic(
    @SerializedName("方法的数量")
    val methodNumber: Int,

    @SerializedName("方法的统计信息")
    val info: NoNeedToTestMethodsInfo,
) {
    companion object {
        fun fromMethodInfos(methodInfos: List<MethodInfo>): NoNeedToTestMethodsStatistic {
            val noNeedToTestMethods = methodInfos.filter { it.methodType == MethodType.NO_NEED_TO_TEST }

            val methodNumber = noNeedToTestMethods.size
            val info = NoNeedToTestMethodsInfo.fromMethodInfos(noNeedToTestMethods)

            return NoNeedToTestMethodsStatistic(methodNumber, info)
        }
    }
}

data class NeedButUnableMethodsStatistic(
    @SerializedName("方法的数量")
    val methodNumber: Int,

    @SerializedName("方法的统计信息")
    val info: List<String>, // List of method signatures
) {
    companion object {
        fun fromMethodInfos(methodInfos: List<MethodInfo>): NeedButUnableMethodsStatistic {
            val needButUnableMethods = methodInfos.filter { it.methodType == MethodType.NEED_BUT_UNABLE }

            val methodNum = needButUnableMethods.size
            val info = needButUnableMethods.map { it.methodSig }

            return NeedButUnableMethodsStatistic(methodNum, info)
        }
    }
}

data class EnableMethodsStatistic(
    @SerializedName("方法的数量")
    val methodNumber: Int,

    @SerializedName("方法的统计信息")
    val info: List<MethodStatistic>,
) {
    companion object {
        fun fromMethodInfos(methodInfos: List<MethodInfo>): EnableMethodsStatistic {
            val enableMethods = methodInfos.filter { it.methodType == MethodType.ENABLE }

            val methodNumber = enableMethods.size
            val info = enableMethods.map { MethodStatistic.fromMethodInfo(it) }

            return EnableMethodsStatistic(methodNumber, info)
        }
    }
}

data class NoNeedToTestMethodsInfo(
    @SerializedName("非公有方法的数量")
    val nonPublicMethodNum: Int,

    @SerializedName("构造方法的数量")
    val constructorMethodNum: Int,

    @SerializedName("匿名方法的数量")
    val anonymousMethodNum: Int,

    @SerializedName("抽象方法的数量")
    val abstractMethodNum: Int,

    @SerializedName("get/set方法的数量")
    val getSetMethodNum: Int,

    @SerializedName("componentN 方法的数量")
    val componentNMethodNum: Int,

    @SerializedName("override 方法的数量")
    val overrideMethodNum: Int,
) {
    companion object {
        fun fromMethodInfos(methodInfos: List<MethodInfo>): NoNeedToTestMethodsInfo {
            val filteredMethodInfos = methodInfos.filter { it.methodType == MethodType.NO_NEED_TO_TEST }

            val counts = filteredMethodInfos.groupingBy { it.methodReason }.eachCount()

            return NoNeedToTestMethodsInfo(
                counts[MethodReason.NON_PUBLIC_METHOD] ?: 0,
                counts[MethodReason.CONSTRUCTOR_METHOD] ?: 0,
                counts[MethodReason.ANONYMOUS_METHOD] ?: 0,
                counts[MethodReason.ABSTRACT_METHOD] ?: 0,
                counts[MethodReason.GET_SET_METHOD] ?: 0,
                counts[MethodReason.COMPONENT_N_METHOD] ?: 0,
                counts[MethodReason.OVERRIDE_METHOD] ?: 0,
            )
        }
    }
}

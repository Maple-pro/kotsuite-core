package org.kotsuite.ga.statistic

import com.google.gson.annotations.SerializedName
import org.kotsuite.ga.report.ClassInfo
import org.kotsuite.ga.report.ClassReason
import org.kotsuite.ga.report.ClassType

data class ClassesStatistic(
    @SerializedName("不需要测试的类的统计信息")
    val noNeedToTestClassesStatistic: NoNeedToTestClassesStatistic,

    @SerializedName("需要测试但是无法测试的类的统计信息")
    val needButUnableClassesStatistic: NeedButUnableClassesStatistic,

    @SerializedName("可以测试的类的统计信息")
    val enableClassesStatistic: EnableClassesStatistic,
) {
    companion object {
        fun fromClassInfos(classInfos: List<ClassInfo>): ClassesStatistic {
            val noNeedToTestClasses = classInfos.filter { it.classType == ClassType.NO_NEED_TO_TEST }
            val noNeedToTestClassesStatistic = NoNeedToTestClassesStatistic.fromClassInfos(noNeedToTestClasses)

            val needButUnableClasses = classInfos.filter { it.classType == ClassType.NEED_BUT_UNABLE }
            val needButUnableClassesStatistic = NeedButUnableClassesStatistic.fromClassInfos(needButUnableClasses)

            val enableClasses = classInfos.filter { it.classType == ClassType.ENABLE }
            val enableClassesStatistic = EnableClassesStatistic.fromClassInfos(enableClasses)

            return ClassesStatistic(
                noNeedToTestClassesStatistic,
                needButUnableClassesStatistic,
                enableClassesStatistic,
            )
        }
    }
}

data class NoNeedToTestClassesStatistic(
    @SerializedName("类的数量")
    val classNumber: Int,

    @SerializedName("类的统计信息")
    val info: NoNeedToTestClassesInfo,
) {
    companion object {
        fun fromClassInfos(classInfos: List<ClassInfo>): NoNeedToTestClassesStatistic {
            val noNeedToTestClasses = classInfos.filter { it.classType == ClassType.NO_NEED_TO_TEST }

            val classNumber = noNeedToTestClasses.size
            val info = NoNeedToTestClassesInfo.fromClassInfos(noNeedToTestClasses)

            return NoNeedToTestClassesStatistic(classNumber, info)
        }
    }
}

data class NeedButUnableClassesStatistic(
    @SerializedName("类的数量")
    val classNumber: Int,

    @SerializedName("类的统计信息")
    val info: List<String>, // List of class names
) {
    companion object {
        fun fromClassInfos(classInfos: List<ClassInfo>): NeedButUnableClassesStatistic {
            val needButUnableClasses = classInfos.filter { it.classType == ClassType.NEED_BUT_UNABLE }

            val classNumber = needButUnableClasses.size
            val info = needButUnableClasses.map { it.className }

            return NeedButUnableClassesStatistic(classNumber, info)
        }
    }
}

data class EnableClassesStatistic(
    @SerializedName("类的数量")
    val classNumber: Int,

    @SerializedName("类的统计信息")
    val info: List<ClassStatistic>,
) {
    companion object {
        fun fromClassInfos(classInfos: List<ClassInfo>): EnableClassesStatistic {
            val enableClasses = classInfos.filter { it.classType == ClassType.ENABLE }

            val classNumber = enableClasses.size
            val info = enableClasses.map { ClassStatistic.fromClassInfo(it) }

            return EnableClassesStatistic(classNumber, info)
        }
    }
}

data class NoNeedToTestClassesInfo(
    @SerializedName("匿名类的数量")
    val anonymousClassNum: Int,

    @SerializedName("非公共类的数量")
    val nonPublicClassNum: Int,

    @SerializedName("抽象类的数量")
    val abstractClassNum: Int,

    @SerializedName("接口类的数量")
    val interfaceClassNum: Int,

    @SerializedName("数据类的数量")
    val dataClassNum: Int,

    @SerializedName("包级别类的数量")
    val packageLevelClassNum: Int,
) {
    companion object {
        fun fromClassInfos(classInfos: List<ClassInfo>): NoNeedToTestClassesInfo {
            val filteredClassInfo = classInfos.filter { it.classType == ClassType.NO_NEED_TO_TEST }

            val counts = filteredClassInfo.groupingBy { it.reason }.eachCount()

            return NoNeedToTestClassesInfo(
                counts[ClassReason.ANONYMOUS_CLASS] ?: 0,
                counts[ClassReason.NON_PUBLIC_CLASS] ?: 0,
                counts[ClassReason.ABSTRACT_CLASS] ?: 0,
                counts[ClassReason.INTERFACE_CLASS] ?: 0,
                counts[ClassReason.DATA_CLASS] ?: 0,
                counts[ClassReason.PACKAGE_LEVEL_CLASS] ?: 0,
            )
        }
    }
}


package org.kotsuite.ga.report

import org.kotsuite.soot.extensions.*
import soot.SootClass

enum class ClassType {
    // 不需要测试的类
    NO_NEED_TO_TEST,

    // 需要测试但是无法测试的类
    NEED_BUT_UNABLE,

    // 可以测试的类（包含测试失败的类和测试通过的类）
    ENABLE,
}

enum class ClassReason {
    ANONYMOUS_CLASS,

    NON_PUBLIC_CLASS,

    ABSTRACT_CLASS,

    INTERFACE_CLASS,

    DATA_CLASS,

    PACKAGE_LEVEL_CLASS,

    UNSUPPORTED_CLASS,

    ENABLE_CLASS,
}

fun SootClass.getClassType(): ClassType {
    return when {
        this.isAnonymousClass() || !this.isPublicClass() || this.isAbstractClass() || this.isInterfaceClass()
                || this.isPackageLevel() || this.isDataClass() -> ClassType.NO_NEED_TO_TEST
        this.isUnsupportedClass() -> ClassType.NEED_BUT_UNABLE
        else -> ClassType.ENABLE
    }
}

fun SootClass.getClassReason(): ClassReason {
    return when {
        this.isAnonymousClass() -> ClassReason.ANONYMOUS_CLASS
        !this.isPublicClass() -> ClassReason.NON_PUBLIC_CLASS
        this.isAbstractClass() -> ClassReason.ABSTRACT_CLASS
        this.isInterfaceClass() -> ClassReason.INTERFACE_CLASS
        this.isPackageLevel() -> ClassReason.PACKAGE_LEVEL_CLASS
        this.isDataClass() -> ClassReason.DATA_CLASS
        this.isUnsupportedClass() -> ClassReason.UNSUPPORTED_CLASS
        else -> ClassReason.ENABLE_CLASS
    }
}

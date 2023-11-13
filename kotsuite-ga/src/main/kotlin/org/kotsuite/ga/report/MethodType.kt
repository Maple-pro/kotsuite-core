package org.kotsuite.ga.report

import org.kotsuite.soot.extensions.*
import soot.SootMethod

enum class MethodType {
    NO_NEED_TO_TEST,    // 不需要测试的方法

    NEED_BUT_UNABLE,    // 需要测试但是无法测试的方法

    ENABLE,             // 可以测试的方法
}

enum class MethodReason {
    // 非 public 方法
    NON_PUBLIC_METHOD,

    CONSTRUCTOR_METHOD,

    // 匿名方法
    ANONYMOUS_METHOD,

    // 抽象方法
    ABSTRACT_METHOD,

    GET_SET_METHOD,

    COMPONENT_N_METHOD,

    OVERRIDE_METHOD,

    UNSUPPORTED_METHOD,

    ENABLE_METHOD,
}

fun SootMethod.getMethodType(): MethodType {
    return when {
        !this.isPublicMethod() || this.isConstructorMethod() || this.isAnonymousMethod() || this.isAbstractMethod()
                || this.isGetSetMethod() || this.isComponentNMethod() || this.isOverrideMethod() -> MethodType.NO_NEED_TO_TEST
        this.isUnsupportedMethod() -> MethodType.NEED_BUT_UNABLE
        else -> MethodType.ENABLE
    }
}

fun SootMethod.getMethodReason(): MethodReason {
    return when {
        !this.isPublicMethod() -> MethodReason.NON_PUBLIC_METHOD
        this.isConstructorMethod() -> MethodReason.CONSTRUCTOR_METHOD
        this.isAnonymousMethod() -> MethodReason.ANONYMOUS_METHOD
        this.isAbstractMethod() -> MethodReason.ABSTRACT_METHOD
        this.isGetSetMethod() -> MethodReason.GET_SET_METHOD
        this.isComponentNMethod() -> MethodReason.COMPONENT_N_METHOD
        this.isOverrideMethod() -> MethodReason.OVERRIDE_METHOD
        this.isUnsupportedMethod() -> MethodReason.UNSUPPORTED_METHOD
        else -> MethodReason.ENABLE_METHOD
    }
}

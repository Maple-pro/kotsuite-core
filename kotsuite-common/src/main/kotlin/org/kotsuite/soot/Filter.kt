package org.kotsuite.soot

import org.kotsuite.soot.extensions.*
import soot.SootClass
import soot.SootMethod

object Filter {
    fun testSuiteGeneratorClassFilter(sootClass: SootClass): Boolean {
        return !sootClass.isAnonymousClass()                // 该类是否是匿名类
                && sootClass.isPublicClass()                // 该类是否是公共类
                && !sootClass.isAbstractClass()             // 该类是否是抽象类
                && !sootClass.isInterfaceClass()            // 该类是否是接口
                && !sootClass.isPackageLevel()                    // 该类是否是 Object
                && !sootClass.isDataClass()                 // 该类是否是数据类
//                && !sootClass.isAndroidClass()
    }

    fun testSuiteGeneratorMethodFilter(sootMethod: SootMethod): Boolean {
        return sootMethod.isPublicMethod()
                && !sootMethod.isConstructorMethod()        // 该函数是否是构造函数
                && !sootMethod.isAnonymousMethod()          // 该函数是否是匿名函数
                && !sootMethod.isAbstractMethod()           // 该函数是否是抽象函数
//                && !sootMethod.isAndroidParameterMethod()
                && !sootMethod.isGetSetMethod()             // 该函数是否是 Get/Set 函数
//                && !sootMethod.isObjectMethod()
                && !sootMethod.isComponentNMethod()         // 该函数是否是 componentN 函数
                && !sootMethod.isOverrideMethod()           // 该函数是否是 Android 类中函数的重载, e.g., onCreate()
                && !sootMethod.isUnsupportedMethod()        // 函数参数中是否包含未支持的类型
    }
}
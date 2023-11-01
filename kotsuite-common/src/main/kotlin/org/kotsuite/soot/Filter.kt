package org.kotsuite.soot

import org.kotsuite.CommonClassConstants
import org.kotsuite.soot.SootUtils.getConstructor
import soot.RefType
import soot.SootClass
import soot.SootMethod

object Filter {
    fun testSuiteGeneratorClassFilter(sootClass: SootClass): Boolean {
        return !sootClass.isAnonymousClass()
                && sootClass.isPublicClass()
                && !sootClass.isAbstractClass()
                && !sootClass.isInterfaceClass()
                && !sootClass.isObject()
                && !sootClass.isDataClass()
//                && !sootClass.isAndroidClass()
    }

    fun testSuiteGeneratorMethodFilter(sootMethod: SootMethod): Boolean {
        return sootMethod.isPublicMethod()
                && !sootMethod.isConstructorMethod()
                && !sootMethod.isAnonymousMethod()
                && !sootMethod.isAbstractMethod()
//                && !sootMethod.isAndroidParameterMethod()
                && !sootMethod.isGetSetMethod()
//                && !sootMethod.isObjectMethod()
                && !sootMethod.isComponentNMethod()
                && !sootMethod.isOverrideMethod()
    }

    private fun SootMethod.isConstructorMethod(): Boolean {
        return (this.subSignature.equals("void <init>()") || this.name.equals("<init>"))
    }

    /**
     * 类是否继承自 Android 类，或构造函数参数包含 Android 类
     */
    fun SootClass.isAndroidClass(): Boolean {
        // 类是否继承自 Android 内部类
        if (this.isAndroidRelatedClass()) {
            return true
        }

        // 构造函数参数是否包含 Android 内部类
        val constructorMethod = this.getConstructor() ?: return true
        for (type in constructorMethod.parameterTypes) {
            if (type is RefType && type.sootClass.isAndroidRelatedClass()) {
                return true
            }
        }

        return false
    }

    /**
     * 类是否继承自 Android 类
     */
    private fun SootClass.isAndroidRelatedClass(): Boolean {
        var curClass = this
        while (curClass.name != CommonClassConstants.object_class_name || curClass.hasSuperclass()) {
            if (curClass.name.startsWith("android")) return true

            curClass = curClass.superclass
        }
        return curClass.name.startsWith("android")
    }

    private fun SootClass.isPublicClass(): Boolean {
        return this.isPublic
    }

    private fun SootClass.isAnonymousClass(): Boolean {
        return this.name.contains("$")
    }

    private fun SootClass.isAbstractClass(): Boolean {
        return this.isAbstract
    }

    private fun SootClass.isInterfaceClass(): Boolean {
        return this.isInterface
    }

    private fun SootClass.isObject(): Boolean {
        return this.shortName.endsWith("Kt")
    }

    private fun SootClass.isDataClass(): Boolean {
        val constructor = this.getConstructor() ?: return false
        val constructorParamCount = constructor.parameterCount
        if (constructorParamCount == 0) return false

        var isDataClass = true
        for (i in 1..constructorParamCount) {
            val componentMethodName = "component$i"
            isDataClass = this.declaresMethodByName(componentMethodName)
        }

        return isDataClass
    }

    private fun SootMethod.isPublicMethod(): Boolean {
        return this.isPublic
    }

    private fun SootMethod.isAnonymousMethod(): Boolean {
        return this.name.contains("$")
    }

    private fun SootMethod.isAbstractMethod(): Boolean {
        return this.isAbstract
    }

    private fun SootMethod.isAndroidParameterMethod(): Boolean {
        val parameterTypes = this.parameterTypes
        parameterTypes.forEach {
            if (it.toString().startsWith("android")) return true
        }
        return false
    }

    private fun SootMethod.isGetSetMethod(): Boolean {
        val sootClass = this.declaringClass
        val classFields = sootClass.fields
        classFields.forEach {
            if (this.name.contains(it.name, true)
                && (this.name.startsWith("get") || this.name.startsWith("set"))
            ) {
                return true
            }
        }

        return false
    }

    @Deprecated(
        "Can be replaced by `isOverrideMethod`",
        ReplaceWith("sootMethod.isOverrideMethod()")
    )
    private fun SootMethod.isObjectMethod(): Boolean {
        return this.name == "toString"
                || this.name == "hashCode"
                || this.name == "equals"
    }

    private fun SootMethod.isComponentNMethod(): Boolean {
        val methodNamePattern = "component\\d+".toRegex()
        return this.name.matches(methodNamePattern)
    }

    private fun SootMethod.isOverrideMethod(): Boolean {
        val sootClass = this.declaringClass

        val allSuperMethods = this.getAllSuperMethods(sootClass)

        val ignoredSuperMethods = allSuperMethods
            .filter {
                !it.declaringClass.name.equals(sootClass.name)
            }.filter {
                val className = it.declaringClass.name
                className.startsWith("android") || className.equals(CommonClassConstants.object_class_name)
            }

        return ignoredSuperMethods.isNotEmpty()
    }

    private fun SootMethod.getAllSuperMethods(sootClass: SootClass): List<SootMethod> {
        val results = mutableListOf<SootMethod>()

        val candidates = sootClass.getMethodsByNameAndParamCount(this.name, this.parameterCount)
        results.addAll(candidates)

        if (sootClass.hasSuperclass()) {
            val superClass = sootClass.superclass
            results.addAll(this.getAllSuperMethods(superClass))
        }

        val interfaces = sootClass.interfaces
        interfaces.forEach {
            results.addAll(this.getAllSuperMethods(it))
        }

        return results
    }
}
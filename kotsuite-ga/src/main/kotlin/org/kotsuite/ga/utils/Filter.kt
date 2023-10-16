package org.kotsuite.ga.utils

import org.kotsuite.utils.SootUtils.getConstructor
import soot.RefType
import soot.SootClass
import soot.SootMethod

object Filter {
    fun testSuiteGeneratorClassFilter(sootClass: SootClass): Boolean {
        return !anonymousClassFilter(sootClass)
                && publicClassFilter(sootClass)
                && !abstractClassFilter(sootClass)
                && !interfaceClassFilter(sootClass)
                && !dataClassFilter(sootClass)
                && !androidClassFilter(sootClass)
    }

    fun testSuiteGeneratorMethodFilter(sootMethod: SootMethod): Boolean {
        return publicMethodFilter(sootMethod)
                && !constructorMethodFilter(sootMethod)
                && !anonymousMethodFilter(sootMethod)
                && !abstractMethodFilter(sootMethod)
                && !androidParameterMethodFilter(sootMethod)
                && !getSetMethodFilter(sootMethod)
                && !objectMethodFilter(sootMethod)
                && !componentNMethodFilter(sootMethod)
    }

    /**
     * Constructor method filter. If the method is constructor, it returns true.
     *
     * @param sootMethod
     * @return
     */
    fun constructorMethodFilter(sootMethod: SootMethod): Boolean {
        return (sootMethod.subSignature.equals("void <init>()") || sootMethod.name.equals("<init>"))
    }

    private fun androidClassFilter(sootClass: SootClass): Boolean {
        // 类是否继承自 Android 内部类
        if (isAndroidRelatedClass(sootClass)) {
            return true
        }

        // 构造函数参数是否包含 Android 内部类
        val constructorMethod = sootClass.getConstructor() ?: return true
        for (type in constructorMethod.parameterTypes) {
            if (type is RefType && isAndroidRelatedClass(type.sootClass)) {
                return true
            }
        }

        return false
    }

    private fun isAndroidRelatedClass(sootClass: SootClass): Boolean {
        var curClass = sootClass
        while (curClass.name != "java.lang.Object" || curClass.hasSuperclass()) {
            if (curClass.name.startsWith("android")) return true

            curClass = curClass.superclass
        }
        return curClass.name.startsWith("android")
    }

    fun publicClassFilter(sootClass: SootClass): Boolean {
        return sootClass.isPublic
    }

    fun anonymousClassFilter(sootClass: SootClass): Boolean {
        return sootClass.name.contains("$")
    }

    fun abstractClassFilter(sootClass: SootClass): Boolean {
        return sootClass.isAbstract
    }

    fun interfaceClassFilter(sootClass: SootClass): Boolean {
        return sootClass.isInterface
    }

    fun dataClassFilter(sootClass: SootClass): Boolean {
        sootClass.fields.forEach { field ->
            val fieldName = field.name
            val capitalizedFieldName = fieldName.replaceFirstChar { it.uppercase() }

            try {
                // Check for standard getter method
                val getterMethod = sootClass.getMethod("get$capitalizedFieldName", emptyList(), field.type)
                if (getterMethod == null || !getterMethod.isPublic || getterMethod.isStatic) {
                    return false
                }

                // Check for standard setter method
                val setterMethod = sootClass.getMethod("set$capitalizedFieldName", listOf(field.type), soot.VoidType.v())
                if (setterMethod == null || !setterMethod.isPublic || setterMethod.isStatic) {
                    return false
                }
            } catch (e: Exception) {
                return false
            }
        }

        return true
    }

    fun publicMethodFilter(sootMethod: SootMethod): Boolean {
        return sootMethod.isPublic
    }

    fun anonymousMethodFilter(sootMethod: SootMethod): Boolean {
        return sootMethod.name.contains("$")
    }

    fun abstractMethodFilter(sootMethod: SootMethod): Boolean {
        return sootMethod.isAbstract
    }

    fun androidParameterMethodFilter(sootMethod: SootMethod): Boolean {
        val parameterTypes = sootMethod.parameterTypes
        parameterTypes.forEach {
            if (it.toString().startsWith("android")) return true
        }
        return false
    }

    fun getSetMethodFilter(sootMethod: SootMethod): Boolean {
        val sootClass = sootMethod.declaringClass
        val classFields = sootClass.fields
        classFields.forEach {
            if (sootMethod.name.contains(it.name, true)
                && (sootMethod.name.startsWith("get") || sootMethod.name.startsWith("set"))) {
                return true
            }
        }

        return false
    }

    fun objectMethodFilter(sootMethod: SootMethod): Boolean {
        return sootMethod.name == "toString"
                || sootMethod.name == "hashCode"
                || sootMethod.name == "equals"
    }

    fun componentNMethodFilter(sootMethod: SootMethod): Boolean {
        val methodNamePattern = "component\\d+".toRegex()
        return sootMethod.name.matches(methodNamePattern)
    }
}
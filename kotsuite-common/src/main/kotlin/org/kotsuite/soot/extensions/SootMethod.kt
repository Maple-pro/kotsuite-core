package org.kotsuite.soot.extensions

import org.kotsuite.CommonClassConstants
import org.kotsuite.soot.Visibility
import soot.*

fun SootMethod.getVisibility(): Visibility {
    val modifiers = this.modifiers

    return if (Modifier.isPublic(modifiers)) {
        Visibility.PUBLIC
    } else if (Modifier.isProtected(modifiers)) {
        Visibility.PROTECTED
    } else if (Modifier.isPrivate(modifiers)) {
        Visibility.PRIVATE
    } else {
        Visibility.PACKAGE
    }
}

/**
 * Get local variable by variable name in a method
 */
fun SootMethod.getLocalByName(localName: String): Local? {
    val body = this.activeBody
    return body.getLocalByName(localName)
}

fun SootMethod.isConstructorMethod(): Boolean {
    return (this.subSignature.equals("void <init>()") || this.name.equals("<init>"))
}

fun SootMethod.isPublicMethod(): Boolean {
    return this.isPublic
}

fun SootMethod.isAnonymousMethod(): Boolean {
    return this.name.contains("$")
}

fun SootMethod.isAbstractMethod(): Boolean {
    return this.isAbstract
}

/**
 * TODO: 该方法是否应该由 Android Test 进行测试
 * Is android parameter method
 */
fun SootMethod.isAndroidTestMethod(): Boolean {
//    val parameterTypes = this.parameterTypes
//    parameterTypes.forEach {
//        if (it.toString().startsWith("android")) return true
//    }
    return false
}

fun SootMethod.isGetSetMethod(): Boolean {
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
fun SootMethod.isObjectMethod(): Boolean {
    return this.name == "toString"
            || this.name == "hashCode"
            || this.name == "equals"
}

fun SootMethod.isComponentNMethod(): Boolean {
    val methodNamePattern = "component\\d+".toRegex()
    return this.name.matches(methodNamePattern)
}

fun SootMethod.isOverrideMethod(): Boolean {
    val lifeCycleMethods = listOf("onCreate", "onStart", "onResume", "onPause", "onStop", "onDestroy")
    if (lifeCycleMethods.contains(this.name)) {
        return true
    }

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

fun SootMethod.getAllSuperMethods(sootClass: SootClass): List<SootMethod> {
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

fun SootMethod.isUnsupportedMethod(): Boolean {
    val parameters = this.parameterTypes
    return parameters
        .filterIsInstance<RefType>().any {
            it.className.startsWith("kotlin.jvm.functions") || it.className.startsWith("java.util.function.Function")
        }
}

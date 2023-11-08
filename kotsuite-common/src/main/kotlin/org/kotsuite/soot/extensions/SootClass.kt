package org.kotsuite.soot.extensions

import org.kotsuite.CommonClassConstants
import org.kotsuite.utils.IDUtils
import soot.*
import soot.jimple.Jimple

fun SootClass.getInstanceName(): String {
    val objName = this.shortName.replaceFirstChar { it.lowercaseChar() }
    return "${objName}Obj${IDUtils.getId()}"
}

/**
 * Get constructor of a soot class
 */
fun SootClass.getConstructor(): SootMethod? {
    return try {
        this.getMethodByName("<init>")
    } catch (ex: RuntimeException) {
        if (this.methods.none { it.name == "<init>" }) {
            return null
        }
        this.methods.first { it.name == "<init>" }
    }
}

/**
 * Create init method for a soot class
 *
 * @return init method
 */
fun SootClass.generateInitMethod(): SootMethod {
    val jimple = Jimple.v()

    // Create a constructor method
    val constructorMethod = SootMethod("<init>", null, VoidType.v(), Modifier.PUBLIC)

    val body = jimple.newBody(constructorMethod)
    constructorMethod.activeBody = body

    // Add `this` local variable to the constructor body
    val thisLocal = jimple.newLocal("this", this.type)
    body.locals.add(thisLocal)
    val thisStmt = jimple.newIdentityStmt(thisLocal, jimple.newThisRef(this.type))
    body.units.add(thisStmt)

    // Call the superclass constructor using `super()`
    val objectClass = Scene.v().getSootClass(CommonClassConstants.object_class_name)
    val objectConstructorRef = Scene.v().makeConstructorRef(objectClass, listOf())
    val superInvokeStmt = jimple.newInvokeStmt(jimple.newSpecialInvokeExpr(thisLocal, objectConstructorRef))
    body.units.add(superInvokeStmt)

    // Add return void statement
    body.units.add(jimple.newReturnVoidStmt())

    return constructorMethod
}

/**
 * Check if a SootClass is an object class in kotlin
 */
fun SootClass.isObject(): Boolean {
    this.fields.forEach {
        if (it.name == "INSTANCE" && it.type.toString() == this.type.toString()) {
            return true
        }
    }

    return false
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
fun SootClass.isAndroidRelatedClass(): Boolean {
    var curClass = this
    while (curClass.name != CommonClassConstants.object_class_name || curClass.hasSuperclass()) {
        if (curClass.name.startsWith("android")) return true

        curClass = curClass.superclass
    }
    return curClass.name.startsWith("android")
}

fun SootClass.isPublicClass(): Boolean {
    return this.isPublic
}

fun SootClass.isAnonymousClass(): Boolean {
    return this.name.contains("$")
}

fun SootClass.isAbstractClass(): Boolean {
    return this.isAbstract
}

fun SootClass.isInterfaceClass(): Boolean {
    return this.isInterface
}

fun SootClass.isPackageLevel(): Boolean {
    return this.shortName.endsWith("Kt")
}

fun SootClass.isDataClass(): Boolean {
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

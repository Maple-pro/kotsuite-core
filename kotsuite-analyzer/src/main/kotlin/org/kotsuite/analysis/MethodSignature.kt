package org.kotsuite.analysis

data class MethodSignature(
    val clazz: String, val returnType: String, val methodName: String, val params: Array<String>)

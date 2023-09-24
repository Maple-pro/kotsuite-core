package org.kotsuite.analysis

data class MethodSignature(
    val clazz: String, val returnType: String, val methodName: String, val params: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MethodSignature

        if (clazz != other.clazz) return false
        if (returnType != other.returnType) return false
        if (methodName != other.methodName) return false
        if (!params.contentEquals(other.params)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clazz.hashCode()
        result = 31 * result + returnType.hashCode()
        result = 31 * result + methodName.hashCode()
        result = 31 * result + params.contentHashCode()
        return result
    }
}

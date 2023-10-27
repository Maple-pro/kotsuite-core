package org.kotsuite.utils

import soot.*

object ASMUtils {

    fun SootClass.getClassDescriptor(): String {
        return "L" + this.name.replace('.', '/') + ";"
    }

    /**
     * Convert to ASM method description
     *
     * @return e.g., (Ljava/lang/String;I)I
     */
    fun SootMethod.getMethodDescription(): String {
        val returnType = this.returnType
        val parameterTypes = this.parameterTypes
        val stringBuilder = StringBuilder()
        stringBuilder.append("(")
        parameterTypes.forEach {
            stringBuilder.append(sootType2ASMType(it))
        }
        stringBuilder.append(")")
        stringBuilder.append(sootType2ASMType(returnType))

        return stringBuilder.toString()
    }

    private fun sootType2ASMType(type: Type): String {
        return when (type) {
            is VoidType -> "V"
            is BooleanType -> "Z"
            is ByteType -> "B"
            is CharType -> "C"
            is IntType -> "I"
            is DoubleType -> "D"
            is FloatType -> "F"
            is LongType -> "L"
            is ShortType -> "S"
            is RefType -> {
                val className = type.className.replace('.', '/')
                "L$className;"
            }
            else -> ""
        }
    }
}
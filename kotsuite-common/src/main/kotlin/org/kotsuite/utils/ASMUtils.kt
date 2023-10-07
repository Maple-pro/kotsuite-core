package org.kotsuite.utils

import soot.BooleanType
import soot.ByteType
import soot.CharType
import soot.DoubleType
import soot.FloatType
import soot.IntType
import soot.LongType
import soot.RefType
import soot.ShortType
import soot.SootMethod
import soot.Type
import soot.VoidType

object ASMUtils {
    /**
     * Convert to ASM method description
     *
     * @param method sootMethod, e.g., int methodName(java.lang.String, int)
     * @return e.g., (Ljava/lang/String;I)I
     */
    fun getMethodDescription(method: SootMethod): String {
        val returnType = method.returnType
        val parameterTypes = method.parameterTypes
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
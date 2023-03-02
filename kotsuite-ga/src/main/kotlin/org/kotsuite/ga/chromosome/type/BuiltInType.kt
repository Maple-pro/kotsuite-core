package org.kotsuite.ga.chromosome.type

import soot.Type
import soot.BooleanType
import soot.ByteType
import soot.CharType
import soot.DoubleType
import soot.IntType
import soot.NullType

enum class BuiltInType {
    INT,
    STRING,
    DOUBLE,
    CHAR,
    BYTE,
    BOOLEAN,
    NULL;

    fun sootTypeToBuiltInType(type: Type) {
        when (type) {
            is IntType -> INT
            is DoubleType -> DOUBLE
            is CharType -> CHAR
            is ByteType -> BYTE
            is BooleanType -> BOOLEAN
            is NullType -> NULL
        }
    }
}
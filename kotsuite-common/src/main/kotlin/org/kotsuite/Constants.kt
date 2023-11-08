package org.kotsuite

import org.kotsuite.soot.Visibility
import soot.*

object MockitoConstants {
    const val mockito_class_name = "org.mockito.Mockito"

    const val mock_method_sig = "<org.mockito.Mockito: java.lang.Object mock(java.lang.Class)>"

    const val spy_method_sig = "<org.mockito.Mockito: java.lang.Object spy(java.lang.Class)>"

    const val when_method_sig = "<org.mockito.Mockito: org.mockito.stubbing.OngoingStubbing when(java.lang.Object)>"

    const val thenReturn_method_sig = "<org.mockito.stubbing.OngoingStubbing: java.lang.Object thenReturn(java.lang.Object)>"

    const val onGoingStubbing_class_name = "org.mockito.stubbing.OngoingStubbing"
}

object JMockKConstants {
    const val jmockk_class_name = "io.github.maples.jmockk.JMockK"

    const val mockk_method_sig = "<io.github.maples.jmockk.JMockK: java.lang.Object mockk(java.lang.Class,boolean)>"

    const val spyk_class_method_sig = "<io.github.maples.jmockk.JMockK: java.lang.Object spyk(java.lang.Class)>"

    const val spyk_object_method_sig = "<io.github.maples.jmockk.JMockK: java.lang.Object spyk(java.lang.Object)>"

    const val mockkStatic_method_sig = "<io.github.maples.jmockk.JMockK: void mockkStatic(java.lang.Class[])>"

    const val mockkObject_method_sig = "<io.github.maples.jmockk.JMockK: void mockkObject(java.lang.Class[])>"

    const val when_method_sig = "<io.github.maples.jmockk.JMockK: io.github.maples.jmockk.stubbing.OngoingStubbing when(java.lang.Object,io.github.maples.jmockk.Visibility,java.lang.String,java.lang.Object[])>"

    const val whenStatic_method_sig = "<io.github.maples.jmockk.stubbing.OngoingStubbing whenStatic(java.lang.Class,java.lang.Boolean,io.github.maples.jmockk.Visibility,java.lang.String,java.lang.Object[])>"

    const val ongoingStubbing_class_name = "io.github.maples.jmockk.stubbing.OngoingStubbing"

    const val thenReturn_method_sig = "<io.github.maples.jmockk.stubbing.OngoingStubbing: void thenReturn(java.lang.Object)>"

    const val visibility_class_name = "io.github.maples.jmockk.Visibility"

    fun Visibility.getVisibilityFieldSig(): String {
        return when(this) {
            Visibility.PUBLIC -> {
                "<io.github.maples.jmockk.Visibility: io.github.maples.jmockk.Visibility PUBLIC>"
            }
            Visibility.PROTECTED -> {
                "<io.github.maples.jmockk.Visibility: io.github.maples.jmockk.Visibility PROTECTED>"
            }
            Visibility.PACKAGE -> {
                "<io.github.maples.jmockk.Visibility: io.github.maples.jmockk.Visibility PACKAGE>"
            }
            Visibility.PRIVATE -> {
                "<io.github.maples.jmockk.Visibility: io.github.maples.jmockk.Visibility PRIVATE>"
            }
        }
    }
}

object AssertionConstants {
    const val assertion_class_name = "org.junit.Assert"

    const val assertEquals_method_sig = "<org.junit.Assert: void assertEquals(java.lang.Object,java.lang.Object)>"
}

object PrimitiveConstants {
    const val boolean_class_name = "java.lang.Boolean"
    const val boolean_constructor_method_sig = "<java.lang.Boolean: void <init>(boolean)>"

    const val byte_class_name = "java.lang.Byte"
    const val byte_constructor_method_sig = "<java.lang.Byte: void <init>(byte)>"

    const val character_class_name = "java.lang.Character"
    const val character_constructor_method_sig = "<java.lang.Character: void <init>(char)>"

    const val double_class_name = "java.lang.Double"
    const val double_constructor_method_sig = "<java.lang.Double: void <init>(double)>"

    const val float_class_name = "java.lang.Float"
    const val float_constructor_method_sig = "<java.lang.Float: void <init>(float)>"

    const val integer_class_name = "java.lang.Integer"
    const val integer_constructor_method_sig = "<java.lang.Integer: void <init>(int)>"

    const val long_class_name = "java.lang.Long"
    const val long_constructor_method_sig = "<java.lang.Long: void <init>(long)>"

    const val string_class_name = "java.lang.String"
    const val string_constructor_method_sig = "<java.lang.String: void <init>()>"
}

object PrintConstants {
    const val printStream_class_name = "java.io.PrintStream"
    const val println_boolean_method_sig = "<java.io.PrintSteam: void println(boolean)>"
    const val println_char_method_sig = "<java.io.PrintStream: void println(char)>"
    const val println_int_method_sig = "<java.io.PrintStream: void println(int)>"
    const val println_long_method_sig = "<java.io.PrintStream: void println(long)>"
    const val println_float_method_sig = "<java.io.PrintStream: void println(float)>"
    const val println_double_method_sig = "<java.io.PrintStream: void println(double)>"
    const val println_string_method_sig = "<java.io.PrintStream: void println(java.lang.String)>"
    const val println_object_method_sig = "<java.io.PrintStream: void println(java.lang.Object)>"

    const val out_field_sig = "<java.lang.System: java.io.PrintStream out>"

    fun getPrintlnSig(type: Type): String {
        return when(type) {
            is BooleanType -> println_boolean_method_sig
            is CharType -> println_char_method_sig
            is IntType -> println_int_method_sig
            is LongType -> println_long_method_sig
            is FloatType -> println_float_method_sig
            is DoubleType -> println_double_method_sig
            RefType.v("java.lang.String") -> println_string_method_sig
            is RefType -> println_object_method_sig
            else -> throw Exception("Unsupported println parameter type: $type")
        }
    }
}

object CommonClassConstants {
    const val object_class_name = "java.lang.Object"
    const val string_class_name = "java.lang.String"
}

object ValueOfConstants {
    const val boolean_valueOf_method_sig = "<java.lang.Boolean: java.lang.Boolean valueOf(boolean)>"
    const val byte_valueOf_method_sig = "<java.lang.Byte: java.lang.Byte valueOf(byte)>"
    const val character_valueOf_method_sig = "<java.lang.Character: java.lang.Character valueOf(char)>"
    const val double_valueOf_method_sig = "<java.lang.Double: java.lang.Double valueOf(double)>"
    const val float_valueOf_method_sig = "<java.lang.Float: java.lang.Float valueOf(float)>"
    const val integer_valueOf_method_sig = "<java.lang.Integer: java.lang.Integer valueOf(int)>"
    const val long_valueOf_method_sig = "<java.lang.Long: java.lang.Long valueOf(long)>"
    const val short_valueOf_method_sig = "<java.lang.Short: java.lang.Short valueOf(short)>"

    fun PrimType.getValueOfSig(): String {
        return when (this) {
            is BooleanType -> boolean_valueOf_method_sig
            is ByteType -> byte_valueOf_method_sig
            is CharType -> character_valueOf_method_sig
            is DoubleType -> double_valueOf_method_sig
            is FloatType -> float_valueOf_method_sig
            is IntType -> integer_valueOf_method_sig
            is LongType -> long_valueOf_method_sig
            is ShortType -> short_valueOf_method_sig
            else -> throw Exception("Unsupported primitive type: $this")
        }
    }
}

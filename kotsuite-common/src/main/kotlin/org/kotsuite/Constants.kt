package org.kotsuite

object ObjectConstants {
    const val OBJECT_CLASS_NAME = "java.lang.Object"
}

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

    const val when_method_sig = "<io.github.maples.jmockk.stubbing.OngoingStubbing when(java.lang.Object,io.github.maples.jmockk.Visibility,java.lang.String,java.lang.Object[])>"

    const val whenStatic_method_sig = "<io.github.maples.jmockk.stubbing.OngoingStubbing whenStatic(java.lang.Class,java.lang.Boolean,io.github.maples.jmockk.Visibility,java.lang.String,java.lang.Object[])>"

    const val ongoingStubbing_class_name = "io.github.maples.jmockk.stubbing.OngoingStubbing"

    const val visibility_class_name = "io.github.maples.jmockk.Visibility"
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
    const val string_constructor_method_sig = "<java.lang.String: void <init>(string)>"
}

package org.kotsuite.client

import org.junit.jupiter.api.Test
import org.kotsuite.analysis.MethodSignature

class MainTest {
    @Test
    fun testMain() {
        val myApplicationPath = "../example-projects/MyApplication"
        val classesOrPackagesToAnalyze = "com.example.application" // Use '&' to joint multiple classes or packages

        val args = arrayOf(myApplicationPath, classesOrPackagesToAnalyze)
        main(args)
    }
}
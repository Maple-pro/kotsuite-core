package org.kotsuite.client

import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun testMain() {
        val myApplicationPath = "../example-projects/MyApplication"
        val classesOrPackagesToAnalyze = "com.example.myapplication" // Use '&' to joint multiple classes or packages

        val args = arrayOf(myApplicationPath, classesOrPackagesToAnalyze)
        main(args)
    }
}
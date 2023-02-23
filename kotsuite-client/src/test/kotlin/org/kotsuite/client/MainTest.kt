package org.kotsuite.client

import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun testMain() {
        val myApplicationPath = "../example-projects/MyApplication"
        val classesOrPackagesToAnalyze = "com.example.myapplication.Example" // Use '&' to joint multiple classes or packages
        val gaStrategy = "random"

        val args = arrayOf(myApplicationPath, classesOrPackagesToAnalyze, gaStrategy)
        main(args)
    }
}
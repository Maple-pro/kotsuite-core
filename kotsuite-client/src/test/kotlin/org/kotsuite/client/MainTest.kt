package org.kotsuite.client

import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun testMain() {
        val myApplicationPath = "../example-projects/MyApplication"
        // Use '&' to joint multiple classes or packages
        val classesOrPackagesToAnalyze = "com.example.myapplication.Example&com.example.myapplication.Callee"
        val libsPath = "../libs"
        val gaStrategy = "random"

        val args = arrayOf(
            "--project", myApplicationPath,
            "--includes", classesOrPackagesToAnalyze,
            "--libs", libsPath,
            "--strategy", gaStrategy)
        main(args)
    }
}
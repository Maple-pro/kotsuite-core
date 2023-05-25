package org.kotsuite.client

import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun testMain() {
        val projectPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/example-projects/MyApplication"
        val modulePath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/example-projects/MyApplication/app"
        val includeRules = "com.example.myapplication.Example&com.example.myapplication.Callee"
        val libsPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/libs"
        val strategy = "random"  // ga or random
        val moduleClassPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/example-projects/MyApplication/app/build/tmp/kotlin-classes/debug"

        val args = arrayOf(
            "--project", projectPath,
            "--module", modulePath,
            "--includes", includeRules,
            "--libs", libsPath,
            "--strategy", strategy,
            "--classpath", moduleClassPath,
        )
        main(args)
    }
}
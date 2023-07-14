package org.kotsuite.client

import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun testMyApplication() {
        val projectPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/example-projects/MyApplication"
        val modulePath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/example-projects/MyApplication/app"
        val moduleClassPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/example-projects/MyApplication/app/build/tmp/kotlin-classes/debug"
        val sourcePath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/example-projects/MyApplication/app/src/main/java"
        val includeRules = "com.example.myapplication.Example&com.example.myapplication.Callee"
        val libsPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/libs"
        val strategy = "ga"  // ga or random

        val args = arrayOf(
            "--project", projectPath,
            "--module", modulePath,
            "--classpath", moduleClassPath,
            "--source", sourcePath,
            "--includes", includeRules,
            "--libs", libsPath,
            "--strategy", strategy,
        )
        main(args)
    }

    @Test
    fun testAlarmClock() {

    }

    @Test
    fun testCalendar() {

    }

    @Test
    fun testExactCalculator() {

    }

    @Test
    fun testGallery2() {

    }

}
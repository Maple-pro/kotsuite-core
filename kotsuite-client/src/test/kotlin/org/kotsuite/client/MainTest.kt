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
        val dependency = Data.myApplicationDependencies.joinToString(":")

        val args = arrayOf(
            "--project", projectPath,
            "--module", modulePath,
            "--classpath", moduleClassPath,
            "--source", sourcePath,
            "--includes", includeRules,
            "--libs", libsPath,
            "--strategy", strategy,
            "--dependency", dependency,
        )
        main(args)
    }

    @Test
    fun testSimpleGallery() {
        val projectPath = "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Gallery"
        val modulePath = "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Gallery/app"
        val moduleClassPath = "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Gallery/app/build/tmp/kotlin-classes/fossDebug"
        val sourcePath = "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Gallery/app/src/main/kotlin"
        val includeRules = "com.simplemobiletools.gallery.pro"
        val libsPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/libs"
        val strategy = "ga"
        val dependency = Data.simpleGalleryDependencies.joinToString(":")

        val args = arrayOf(
            "--project", projectPath,
            "--module", modulePath,
            "--classpath", moduleClassPath,
            "--source", sourcePath,
            "--includes", includeRules,
            "--libs", libsPath,
            "--strategy", strategy,
            "--dependency", dependency,
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
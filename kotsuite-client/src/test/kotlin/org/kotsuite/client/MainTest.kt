package org.kotsuite.client

import org.junit.jupiter.api.Test
import java.io.File

class MainTest {
    @Test
    fun testMyApplication() {
        val projectPath = "/home/yangfeng/Repos/kotsuite-project/test-projects/MyApplication"
        val modulePath = "/home/yangfeng/Repos/kotsuite-project/test-projects/MyApplication/app"
        val moduleClassPath = "/home/yangfeng/Repos/kotsuite-project/test-projects/MyApplication/app/build/tmp/kotlin-classes/debug"
        val sourcePath = "/home/yangfeng/Repos/kotsuite-project/test-projects/MyApplication/app/src/main/java"
        val includeRules = "com.example.myapplication.Example&com.example.myapplication.Callee"
        val libsPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/libs"
        val strategy = "ga"  // ga or random
        val dependency = Data.myApplicationDependencies.joinToString(File.pathSeparator)

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

    /**
     * Test Simple-Gallery test suite generation
     */
    @Test
    fun testSimpleGallery() {
        val projectPath = "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Gallery"
        val modulePath = "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Gallery/app"
        val moduleClassPath = "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Gallery/app/build/tmp/kotlin-classes/fossDebug"
        val sourcePath = "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Gallery/app/src/main/kotlin"
//        val includeRules = "com.simplemobiletools.gallery.pro"
//        val includeRules = "com.simplemobiletools.gallery.pro.adapters.MediaAdapter"
        val includeRules = "com.simplemobiletools.gallery.pro.adapters.DirectoryAdapter"
//        val includeRules = "com.simplemobiletools.gallery.pro.helpers.Config"
//        val includeRules = "com.simplemobiletools.gallery.pro.helpers.MediaFetcher"
//        val includeRules = "com.simplemobiletools.gallery.pro.helpers.FilterThumbnailsManager"
//        val includeRules = "com.simplemobiletools.gallery.pro.helpers.ConstantsKt"
        val libsPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/libs"
        val strategy = "ga"
        val dependency = Data.simpleGalleryDependencies.joinToString(File.pathSeparator)

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
    fun testSimpleCommons() {
        val moduleClassPaths = listOf(
            "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons/build/intermediates/javac/debug/classes",
            "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons/build/tmp/kotlin-classes/debug",
            "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons/build/intermediates/compile_r_class_jar/debug/R.jar!/",
            "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons/build/generated/res/resValues/debug",
        )
        val moduleSourcePaths = listOf(
            "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons/build/generated/ap_generated_sources/debug/out",
            "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons/build/generated/ksp/debug/java",
            "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons/build/generated/ksp/debug/kotlin",
            "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons/build/generated/res/resValues/debug",
            "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons/src/main/kotlin",
            "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons/src/main/res",
        )

        val projectPath = "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons"
        val modulePath = "/home/yangfeng/Repos/kotsuite-project/test-projects/Simple-Commons/commons"
        val moduleClassPath = moduleClassPaths.joinToString(File.pathSeparator)
        val sourcePath = moduleSourcePaths.joinToString(File.pathSeparator)
//        val includeRules = "com.simplemobiletools.commons.helpers.Converters"
//        val includeRules = "com.simplemobiletools.commons.helpers.AlphanumericComparator"
        val includeRules = "com.simplemobiletools.commons"
        val libsPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/libs"
        val strategy = "ga"
        val dependency = Data.simpleCommonsDependencies.joinToString(File.pathSeparator)

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
    fun testKotSuiteCore() {
        val projectPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core"
        val modulePath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/kotsuite-ga"
        val moduleClassPath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/kotsuite-ga/build/classes/kotlin/main"
        val sourcePath = "/home/yangfeng/Repos/kotsuite-project/kotsuite-core/kotsuite-ga/src/main/kotlin"
        val includeRules = "com."
    }

    @Test
    fun testKotlinMath() {
        val projectPath = "/home/yangfeng/Repos/kotsuite-project/test-projects/kotlin-math"
        val modulePath = "/home/yangfeng/Repos/kotsuite-project/test-projects/kotlin-math/src/commonMain"
        val moduleClassPath = "/home/yangfeng/Repos/kotsuite-project/test-projects/kotlin-math/build/classes/kotlin/jvm/main"
        val sourcePath = "/home/yangfeng/Repos/kotsuite-project/test-projects/kotlin-math/src/commonMain/kotlin"
        val includeRules = "dev.romainguy.kotlin.main"
        val strategy = "ga"
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
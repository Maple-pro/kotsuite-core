package org.kotsuite.analysis

import org.kotsuite.Configs
import java.io.File

object Dependency {
    /**
     * Get jars from the `libs/classpath/` directory
     */
    fun getClassPath(): List<String> {
        val classPathLibs = File("${Configs.libsPath}/classpath")
        return getAllJarFilePaths(classPathLibs)
    }

    /**
     * Get test framework jars from the `libs/test-framework/` directory,
     * which is used to generate statements in the test suite.
     */
    fun getTestFramework(): List<String> {
        val testFrameworkLibs = File("${Configs.libsPath}/test-framework")
        return getAllJarFilePaths(testFrameworkLibs)
    }

    /**
     * Get test dependencies from the `libs/test-dependencies/` directory
     */
    fun getTestDependencies(): List<String> {
        val testDependenciesLibs = File("${Configs.libsPath}/test-dependencies")
        return getAllJarFilePaths(testDependenciesLibs)
    }

    private fun getAllJarFilePaths(dir: File): List<String> {
        return if (dir.exists() && dir.isDirectory) {
            val jarFiles = mutableListOf<File>()
            dir.walk()
                .filter { it.isFile && it.extension == "jar" }
                .forEach { jarFiles.add(it) }

            jarFiles.map { it.canonicalPath }
        } else {
            listOf()
        }
    }
}
package org.kotsuite.ga

object Configs {

    lateinit var projectPath: String
    lateinit var modulePath: String
    lateinit var sourceCodePath: String
    lateinit var classesFilePath: String
    lateinit var sootOutputPath: String
    lateinit var outputPath: String
    lateinit var mainClass: String
    lateinit var includeRules: List<String>
    lateinit var includeFiles: String
    lateinit var outputFileDir: String
    lateinit var libsPath: String

    val kotsuiteAgentPath: String
        get() = "$libsPath/kotsuite-agent-1.0-SNAPSHOT.jar"
    val jacocoAgentPath: String
        get() = "$libsPath/org.jacoco.agent-0.8.10-runtime.jar"
    val jacocoCliPath: String
        get() = "$libsPath/org.jacoco.cli-0.8.10-nodeps.jar"
    val kotlinRuntimePath: String
        get() = "$libsPath/kotlin-runtime-1.2.71.jar"
    val kotlinStdLibPath: String
        get() = "$libsPath/kotlin-stdlib-1.8.10.jar"

    const val maxAttempt = 50
    const val targetLineCoverage = 0.6  // Line coverage
    const val targetCCCoverage = 0.9  // Cyclomatic complexity coverage

    fun print(): String {
        return "{ project_path: $projectPath, module_path: $modulePath }"
    }

}
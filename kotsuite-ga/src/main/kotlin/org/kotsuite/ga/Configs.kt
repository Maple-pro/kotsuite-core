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

    var maxAttempt = 50
    val targetLineCoverage = 0.6  // Line coverage
    val targetCCCoverage = 0.9  // Cyclomatic complexity coverage

}
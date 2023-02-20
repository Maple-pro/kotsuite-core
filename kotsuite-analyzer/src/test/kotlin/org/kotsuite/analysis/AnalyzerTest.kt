package org.kotsuite.analysis

import org.junit.jupiter.api.Test

class AnalyzerTest  {
    @Test
    fun testAnalyzeClass() {
        println(System.getProperty("user.dir"))
        val sourceDirector = "./src/test/targets-resources/generated"
        val className = "analysis.Example"
        val methodNames = arrayOf("foo", "bar")

        val analyzer = Analyzer(sourceDirector, listOf(className))
        analyzer.setupSoot(className, methodNames)
        analyzer.runSoot(className, methodNames)
    }

    @Test
    fun testAnalyzeMethod() {
        val myApplicationPath = "../example-projects/MyApplication"
        val classesOrPackagesToAnalyze = "com.example.application" // Use '&' to joint multiple classes or packages

        val className = "com.example.myapplication.FirstFragment" // Class to be analyzed
        val returnType = "void"
        val methodName = "onDestroyView" // Method to be analyzed
        val params = arrayOf<String>()
        val methodSig = MethodSignature(className, returnType, methodName, params)

        val analyzer = Analyzer(myApplicationPath, classesOrPackagesToAnalyze.split("&"))
        analyzer.analyzeMethod(methodSig)
    }

    @Test
    fun testAnalyze() {
        val myApplicationPath = "../example-projects/MyApplication"
        val classesOrPackagesToAnalyze = "com.example.application" // Use '&' to joint multiple classes or packages

        val analyzer = Analyzer(myApplicationPath, classesOrPackagesToAnalyze.split("&"))
        analyzer.analyze()
    }
}
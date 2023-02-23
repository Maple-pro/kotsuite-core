package org.kotsuite.analysis

import org.junit.jupiter.api.Test

class AnalyzerTest  {

    @Test
    fun testAnalyzeMethod() {
        val myApplicationPath = "../example-projects/MyApplication"
        val classesOrPackagesToAnalyze = "com.example.myapplication" // Use '&' to joint multiple classes or packages

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
        val classesOrPackagesToAnalyze = "com.example.myapplication" // Use '&' to joint multiple classes or packages

        val analyzer = Analyzer(myApplicationPath, classesOrPackagesToAnalyze.split("&"))
        analyzer.analyze()
    }
}
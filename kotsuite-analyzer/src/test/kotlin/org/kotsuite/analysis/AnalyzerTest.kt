package org.kotsuite.analysis

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.streams.toList

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

        Analyzer.exampleProjectDir = myApplicationPath
        Analyzer.classesOrPackagesToAnalyze = classesOrPackagesToAnalyze.split("&")
        val sootMethod = Analyzer.analyzeMethod(methodSig)

        assert(sootMethod.activeBody != null)
    }

    @Test
    fun testAnalyze() {
        val myApplicationPath = "../example-projects/MyApplication"
        val classesOrPackagesToAnalyze = "com.example.myapplication" // Use '&' to joint multiple classes or packages

        Analyzer.exampleProjectDir = myApplicationPath
        Analyzer.classesOrPackagesToAnalyze = classesOrPackagesToAnalyze.split("&")
        Analyzer.analyze()

        val expectedClasses = listOf(
            "com.example.myapplication.FirstFragment",
            "com.example.myapplication.MainActivity\$inlined\$sam\$i\$androidx_navigation_ui_AppBarConfiguration_OnNavigateUpListener\$0",
            "com.example.myapplication.MainActivity\$inlined\$sam\$i\$androidx_navigation_ui_AppBarConfiguration_OnNavigateUpListener",
            "com.example.myapplication.MainActivity\$onCreate\$\$inlined\$AppBarConfiguration\$default\$1",
            "com.example.myapplication.MainActivity",
            "com.example.myapplication.SecondFragment"
        )
        assertEquals(expectedClasses, Analyzer.classes.stream().map { it.name }.toList())
    }
}
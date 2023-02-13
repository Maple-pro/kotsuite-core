package org.kotsuite.analysis

import org.junit.jupiter.api.Test
import org.kotsuite.analysis.Analysis

class AnalysisTest  {
    @Test
    fun analysisClass() {
        println(System.getProperty("user.dir"))
        val sourceDirector = "./src/test/targets-resources/generated"
        val className = "analysis.Example"
        val methodNames = listOf("foo", "bar")

        val analysis = Analysis(sourceDirector, className, methodNames)
        analysis.setupSoot()
        analysis.runSoot()
    }
}
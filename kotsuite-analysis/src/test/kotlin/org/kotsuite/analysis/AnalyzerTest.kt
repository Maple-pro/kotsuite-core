package org.kotsuite.analysis

import org.junit.jupiter.api.Test

class AnalyzerTest  {
    @Test
    fun testAnalysisClass() {
        println(System.getProperty("user.dir"))
        val sourceDirector = "./src/test/targets-resources/generated"
        val className = "analysis.Example"
        val methodNames = arrayOf("foo", "bar")

        val analyzer = Analyzer(sourceDirector)
        analyzer.setupSoot(className, methodNames)
        analyzer.runSoot(className, methodNames)
    }

}
package org.kotsuite.analysis

import org.junit.jupiter.api.Test

class AnalysisTest  {
    @Test
    fun analysisClass() {
        println(System.getProperty("user.dir"))
        val sourceDirector = "./src/test/targets-resources/generated"
        val className = "SimpleClass.Example"
        val methodNames = listOf("foo", "bar")

        val analysis = Analysis(sourceDirector, className, methodNames)
        analysis.setupSoot()
        analysis.runSoot()
    }
}
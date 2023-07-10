package org.kotsuite.ga

import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.jimple.JimpleGenerator
import org.kotsuite.ga.chromosome.printer.JasminPrinter
import org.kotsuite.ga.coverage.CoverageGenerator
import org.kotsuite.ga.solution.WholeSolution
import org.kotsuite.ga.strategy.Strategy
import org.slf4j.LoggerFactory
import soot.SootClass

class TestSuiteGenerator(private val gaStrategy: Strategy) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private lateinit var wholeSolution: WholeSolution
    private lateinit var testClasses: List<TestClass>
    private lateinit var jimpleClasses: List<SootClass>

    /**
     * Generate test cases using given strategy
     */
    fun generate() {
        log.info("Generator Strategy: $gaStrategy")

        wholeSolution = gaStrategy.generateWholeSolution()
        testClasses = wholeSolution.classSolutions.map { it.testClass }

        generateJimpleClasses()

        generateCoverageReport()
    }

    /**
     * Print generated test cases to bytecode
     */
    fun printJasminFiles() {
        jimpleClasses.forEach {
            JasminPrinter.printJasminFile(it)
        }
    }

    private fun generateJimpleClasses() {
        jimpleClasses = JimpleGenerator.generateTestClasses(testClasses)
    }

    private fun generateCoverageReport() {
        CoverageGenerator.generate()
    }


}
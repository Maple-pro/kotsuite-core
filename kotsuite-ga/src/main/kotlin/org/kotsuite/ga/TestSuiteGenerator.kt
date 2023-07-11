package org.kotsuite.ga

import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.chromosome.jimple.JimpleGenerator
import org.kotsuite.ga.chromosome.printer.JasminPrinter
import org.kotsuite.ga.coverage.CoverageGenerator
import org.kotsuite.ga.solution.WholeSolution
import org.kotsuite.ga.strategy.Strategy
import org.kotsuite.ga.utils.SootUtils
import org.slf4j.LoggerFactory
import soot.SootClass

class TestSuiteGenerator(private val gaStrategy: Strategy) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private lateinit var wholeSolution: WholeSolution
    private lateinit var testClasses: List<TestClass>
    private lateinit var jimpleClasses: List<SootClass>
    private lateinit var dummyMainClass: SootClass

    /**
     * Generate test cases using given strategy
     */
    fun generate() {
        log.info("Generator Strategy: $gaStrategy")

        wholeSolution = gaStrategy.generateWholeSolution()
        testClasses = wholeSolution.classSolutions.map { it.testClass }

        generateJimpleClasses()

        dummyMainClass = generateDummyMainClass()  // generate dummy main class to get coverage information

        printJasminFiles()

        generateCoverageReport()
    }

    private fun generateJimpleClasses() {
        jimpleClasses = JimpleGenerator.generateTestClassesFromWholeSolution(wholeSolution)
    }

    private fun generateDummyMainClass(): SootClass {
        // Generate main class and main method to call all test cases
        val targetMethods = jimpleClasses
            .map { it.methods }
            .reduce { methods, method -> methods + method }
            .filter { SootUtils.filterConstructorMethod(it) }

        return SootUtils.generateMainClass(Configs.mainClass, targetMethods)
    }

    /**
     * Print generated test cases to bytecode
     */
    private fun printJasminFiles() {
        jimpleClasses.forEach {
            JasminPrinter.printJasminFile(it)
        }
        JasminPrinter.printJasminFile(dummyMainClass)
    }

    private fun generateCoverageReport() {
        CoverageGenerator.generate()
    }

}
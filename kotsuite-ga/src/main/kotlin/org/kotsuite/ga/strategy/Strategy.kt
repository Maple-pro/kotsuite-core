package org.kotsuite.ga.strategy

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.kotsuite.analysis.Analyzer
import org.kotsuite.Configs
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.solution.ClassSolution
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.ga.solution.WholeSolution
import org.kotsuite.ga.utils.Filter
import soot.SootClass
import soot.SootMethod

abstract class Strategy {
    private val log = LogManager.getLogger()

    open fun generateWholeSolution(): WholeSolution {
        log.log(Configs.sectionLevel, "[Whole Solution]")

        // Output the classes that we can analyze
        val classes = Analyzer.classes
            .filter { Filter.testSuiteGeneratorClassFilter(it) }

        val methodMap = mutableMapOf<SootClass, List<SootMethod>>()
        classes.forEach { clazz ->
            methodMap[clazz] = clazz.methods
                .filter { Filter.testSuiteGeneratorMethodFilter(it) }
        }
        val nonEmptyClasses = classes.filter { methodMap.contains(it) && methodMap[it]!!.isNotEmpty() }
        val newMethodMaps =  methodMap.filter { it.value.isNotEmpty() }.entries.sortedBy { it.value.size }
        log.log(Level.INFO, "${nonEmptyClasses.size} classes can be analyzed: $nonEmptyClasses")

//        val classSolutions = Analyzer.classes
//            .filter { Filter.testSuiteGeneratorClassFilter(it) }
//            .map { generateClassSolution(it) }
        val classSolutions = nonEmptyClasses.map {
            try {
                generateClassSolution(it)
            } catch (e: Exception) {
                log.error("Failed to generate class solution for class: $it")
                ClassSolution(it, TestClass("${it.shortName}Test", it.packageName), listOf())
            }
        }

        return WholeSolution(classSolutions)
    }

    @Throws(Exception::class)
    open fun generateClassSolution(targetClass: SootClass): ClassSolution {
        log.log(Configs.sectionLevel, "[Class: ${targetClass.name}]")

        // Generate TestClass for target class
        val testClassName = "${targetClass.shortName}Test"
        val testClass = TestClass(testClassName, targetClass.packageName)

        val methodSolutions = targetClass.methods
            .filter {
                Filter.testSuiteGeneratorMethodFilter(it)
            }
            .map {
                try {
                    generateMethodSolution(it, targetClass)
                } catch (e: Exception) {
                    log.error("Failed to generate method solution for method: $it")
                    MethodSolution(it, listOf())
                }
            }

        return ClassSolution(targetClass, testClass, methodSolutions)
    }

    @Throws(Exception::class)
    abstract fun generateMethodSolution(targetMethod: SootMethod, targetClass: SootClass): MethodSolution

}
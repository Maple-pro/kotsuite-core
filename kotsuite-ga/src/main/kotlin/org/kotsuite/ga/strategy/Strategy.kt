package org.kotsuite.ga.strategy

import com.google.gson.GsonBuilder
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.kotsuite.analysis.Analyzer
import org.kotsuite.Configs
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.solution.ClassSolution
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.ga.solution.WholeSolution
import org.kotsuite.soot.Filter
import soot.SootClass
import soot.SootMethod

abstract class Strategy {
    private val log = LogManager.getLogger()

    private lateinit var targetClasses: List<SootClass>
    private lateinit var targetClassMethodMap: Map<SootClass, List<SootMethod>>
    private var targetMethodsNum = 0

    open fun generateWholeSolution(): WholeSolution {
        log.log(Configs.sectionLevel, "[Whole Solution]")

        // Output the classes that we can analyze
        val filteredClasses = Analyzer.classes
            .filter { Filter.testSuiteGeneratorClassFilter(it) }
            .filter { !it.name.equals("com.simplemobiletools.gallery.pro.helpers.Config") } // [debug only]

        val methodMap = mutableMapOf<SootClass, List<SootMethod>>()
        filteredClasses.forEach { clazz ->
            methodMap[clazz] = clazz.methods.filter {
                Filter.testSuiteGeneratorMethodFilter(it)
            }
        }

        targetClasses = filteredClasses.filter {
            methodMap.contains(it) && methodMap[it]!!.isNotEmpty()
        }

        targetClassMethodMap = methodMap.filter { it.value.isNotEmpty() }

        targetMethodsNum = targetClassMethodMap.entries.sumOf { it.value.size }

        prettyLogTargets()

        val classSolutions = targetClasses.map {
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

        if (!targetClassMethodMap.containsKey(targetClass)) {
            log.log(Level.ERROR, "No method can be analyzed for class: $targetClass")
            return ClassSolution(targetClass, testClass, listOf())
        }

        val methodSolutions = targetClassMethodMap[targetClass]!!
            .map {
                try {
                    generateMethodSolution(it, targetClass)
                } catch (e: Exception) {
                    log.error("Failed to generate method solution for method: $it")
                    log.error("Exception: $e")
                    MethodSolution(it, listOf())
                }
            }

        return ClassSolution(targetClass, testClass, methodSolutions)
    }

    @Throws(Exception::class)
    abstract fun generateMethodSolution(targetMethod: SootMethod, targetClass: SootClass): MethodSolution

    private fun prettyLogTargets() {
        val gson = GsonBuilder().setPrettyPrinting().create()

        val printTargetClassMethodMap = LinkedHashMap<String, List<String>>()
        targetClassMethodMap.forEach { entry ->
            val className = entry.key.name
            val methodNames = entry.value.map { it.subSignature }
            printTargetClassMethodMap[className] = methodNames
        }

        log.info("${targetClasses.size} classes can be analyzed")
        log.debug("\n" + gson.toJson(targetClasses.map { it.name }))

        log.info("$targetMethodsNum methods can be analyzed")
        log.debug("\n" + gson.toJson(printTargetClassMethodMap))
    }

}
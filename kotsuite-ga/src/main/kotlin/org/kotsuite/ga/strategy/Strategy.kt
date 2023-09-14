package org.kotsuite.ga.strategy

import org.apache.logging.log4j.LogManager
import org.kotsuite.analysis.Analyzer
import org.kotsuite.ga.Configs
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.solution.ClassSolution
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.ga.solution.WholeSolution
import org.kotsuite.ga.utils.Filter
import org.kotsuite.ga.utils.SootUtils
import soot.SootClass
import soot.SootMethod

abstract class Strategy {
    private val logger = LogManager.getLogger()

    open fun generateWholeSolution(): WholeSolution {
        logger.log(Configs.sectionLevel, "[Whole Solution]")

        // [test only]
        val classes = Analyzer.classes
            .filter { Filter.testSuiteGeneratorClassFilter(it) }
            .filter {
                it.shortName != "Config" && it.shortName != "AlbumCover" && it.shortName != "DateTaken"
                        && it.shortName != "Directory" && it.shortName != "Favorite" && it.shortName != "FilterItem"
                        && it.shortName != "Medium" && it.shortName != "PaintOptions" && it.shortName != "ThumbnailSection"
                        && it.shortName != "Widget"
            }

        val methodMap = mutableMapOf<SootClass, List<SootMethod>>()
        classes.forEach { clazz ->
            methodMap[clazz] = clazz.methods
                .filter { Filter.testSuiteGeneratorMethodFilter(it) }
        }
        val newMethodMaps =  methodMap.filter { it.value.isNotEmpty() }.entries.sortedBy { it.value.size }
        // [test only]

        val classSolutions = Analyzer.classes
            .filter { Filter.testSuiteGeneratorClassFilter(it) }
            .map { generateClassSolution(it) }

        return WholeSolution(classSolutions)
    }

    open fun generateClassSolution(targetClass: SootClass): ClassSolution {
        logger.log(Configs.sectionLevel, "[Class: ${targetClass.name}]")

        // Generate TestClass for target class
        val testClassName = "${targetClass.shortName}Test"
        val testClass = TestClass(testClassName, targetClass.packageName)

        val methodSolutions = targetClass.methods
            .filter { Filter.testSuiteGeneratorMethodFilter(it) }
            .map { generateMethodSolution(it, targetClass) }

        return ClassSolution(targetClass, testClass, methodSolutions)
    }

    abstract fun generateMethodSolution(targetMethod: SootMethod, targetClass: SootClass): MethodSolution

}
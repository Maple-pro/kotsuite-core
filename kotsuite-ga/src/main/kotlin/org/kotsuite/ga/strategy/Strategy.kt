package org.kotsuite.ga.strategy

import org.kotsuite.analysis.Analyzer
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.solution.ClassSolution
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.ga.solution.WholeSolution
import org.kotsuite.ga.utils.SootUtils
import soot.SootClass
import soot.SootMethod

abstract class Strategy {

    open fun generateWholeSolution(): WholeSolution {
        val classSolutions = Analyzer.classes.map {
            generateClassSolution(it)
        }

        return WholeSolution(classSolutions)
    }

    open fun generateClassSolution(targetClass: SootClass): ClassSolution {
        // Generate TestClass for target class
        val testClassName = "${targetClass.shortName}Test"
        val testClass = TestClass(testClassName, targetClass.packageName)

        val methodSolutions = targetClass.methods
            .filter { SootUtils.filterConstructorMethod(it) }
            .map { generateMethodSolution(it, targetClass) }

        return ClassSolution(targetClass, testClass, methodSolutions)
    }

    abstract fun generateMethodSolution(targetMethod: SootMethod, targetClass: SootClass): MethodSolution

}
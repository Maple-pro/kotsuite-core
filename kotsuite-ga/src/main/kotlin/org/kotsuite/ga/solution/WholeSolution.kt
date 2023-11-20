package org.kotsuite.ga.solution

import org.kotsuite.ga.coverage.ExecResolver

data class WholeSolution(
    val classSolutions: List<ClassSolution>,
) {
    /**
     * Get all successful test cases
     */
    fun getSuccessfulWholeSolution(): WholeSolution {
        val newClassSolutions = classSolutions.map { it.getSuccessfulClassSolution() }
        return WholeSolution(newClassSolutions)
    }

    fun exceptCrashedWholeSolution(): WholeSolution {
        val newClassSolution = classSolutions.map { it.exceptCrashedClassSolution() }
        return WholeSolution(newClassSolution)
    }

    fun updateCoverageInfo(execResolver: ExecResolver) {
        this.classSolutions.forEach { classSolution ->
            val targetClassFitness = execResolver.getFitnessByClassName(classSolution.targetClass.name)
            classSolution.fitness = targetClassFitness
            classSolution.methodSolutions.forEach { methodSolution ->
                val targetMethodFitness = execResolver.getFitnessByMethodSig(methodSolution.targetMethod.signature)
                methodSolution.fitness = targetMethodFitness
            }
        }
    }
}
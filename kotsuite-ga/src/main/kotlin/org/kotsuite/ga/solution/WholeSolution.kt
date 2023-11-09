package org.kotsuite.ga.solution

data class WholeSolution(
    val classSolutions: List<ClassSolution>,
) {
    /**
     * Remove all failure test cases
     */
    fun getSuccessfulWholeSolution(): WholeSolution {
        val newClassSolutions = classSolutions.map { it.getSuccessfulClassSolution() }
        return WholeSolution(newClassSolutions)
    }
}
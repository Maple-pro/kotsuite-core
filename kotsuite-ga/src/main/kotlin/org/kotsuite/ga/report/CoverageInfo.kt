package org.kotsuite.ga.report

import org.kotsuite.ga.coverage.fitness.Fitness

data class CoverageInfo(
    val lineCoverage: Double,
    val ccCoverage: Double,
) {
    companion object {
        fun fromFitness(fitness: Fitness?): CoverageInfo {
            if (fitness == null) {
                return CoverageInfo(0.0, 0.0)
            }
            return CoverageInfo(
                lineCoverage = fitness.lineCoverage,
                ccCoverage = fitness.ccCoverage,
            )
        }
    }
}
package org.kotsuite.ga

import org.kotsuite.ga.strategy.random.RandomStrategy
import org.kotsuite.ga.strategy.standard.StandardGAStrategy

object StrategyHelper {
    fun getGAStrategy(strategy: String, projectDir: String, classesFilePath: String): GAStrategy {

        return when (strategy) {
            "random" -> RandomStrategy
            "ga" -> StandardGAStrategy(50, projectDir, classesFilePath)
            else -> RandomStrategy
        }

    }
}
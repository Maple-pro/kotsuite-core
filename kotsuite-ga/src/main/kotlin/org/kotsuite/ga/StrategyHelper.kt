package org.kotsuite.ga

import org.kotsuite.ga.strategy.random.RandomStrategy

object StrategyHelper {
    fun getGAStrategy(strategy: String): GAStrategy {
        return when (strategy) {
            "random" -> RandomStrategy()
            else -> RandomStrategy()
        }
    }
}
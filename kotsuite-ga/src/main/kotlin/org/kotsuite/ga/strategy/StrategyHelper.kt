package org.kotsuite.ga.strategy

import org.kotsuite.ga.strategy.random.RandomStrategy
import org.kotsuite.ga.strategy.standard.StandardGAStrategy

object StrategyHelper {
    fun getGAStrategy(strategy: String): Strategy {

        return when (strategy) {
            "random" -> RandomStrategy
            "ga" -> StandardGAStrategy
            else -> RandomStrategy
        }

    }
}
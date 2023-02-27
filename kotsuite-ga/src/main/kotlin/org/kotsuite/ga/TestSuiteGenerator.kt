package org.kotsuite.ga

import org.kotsuite.ga.strategy.random.RandomStrategy
import org.slf4j.LoggerFactory

object TestSuiteGenerator {

    private val log = LoggerFactory.getLogger(this.javaClass)

    var gaStrategy: GAStrategy = RandomStrategy()

    fun generate() {
        log.info("Generator Strategy: $gaStrategy")

        gaStrategy.generateTestSuite()
    }
}
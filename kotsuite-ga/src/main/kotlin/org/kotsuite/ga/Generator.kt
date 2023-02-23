package org.kotsuite.ga

import org.kotsuite.ga.random.RandomStrategy
import org.slf4j.LoggerFactory

object Generator {

    private val log = LoggerFactory.getLogger(this.javaClass)

    var gaStrategy: GAStrategy = RandomStrategy()

    fun generate() {
        log.info("Generator Strategy: $gaStrategy")

        gaStrategy.generateTestSuite()
    }
}
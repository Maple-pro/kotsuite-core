package org.kotsuite.client

import org.kotsuite.analysis.Analyzer
import org.slf4j.LoggerFactory

/**
 * This class represents a KotSuite client.
 */
class Client(private var dataDir: String) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Analysis the given bytecode using soot.
     */
    fun analysis() {
        log.info("===Analysis===")
        Analyzer(dataDir).analysis()
    }

    /**
     * Generate test suite for the give bytecode.
     */
    fun generateTestSuite() {

    }

    /**
     * Attach the client to the JVM. Load KotSuite agent on the JVM that runs the code to be tested.
     */
    fun attach() {

    }

    /**
     * Submit generated test case to the JVM attached.
     */
    fun submit() {

    }

    /**
     * Close all connection state to the JVM attached.
     */
    fun close() {

    }

}
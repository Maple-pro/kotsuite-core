package org.kotsuite.client

import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("org.kotsuite.client.Main")
    if (args.isEmpty()) {
        log.error("Args is empty.")
        return
    }

    log.info("===Start main function===")
    val exampleProjectDir = args[0]
    val classesOrPackagesToAnalyze = args[1].split('&')
    val libsPath = args[2]
    val gaStrategy = args[3]
    val client = Client(exampleProjectDir, classesOrPackagesToAnalyze, libsPath, gaStrategy)
    client.analyze()
    client.generateTestSuite()
}


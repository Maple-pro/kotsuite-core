package org.kotsuite.client

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("org.kotsuite.client.Main")

    // Deal with parameter options
    val options = Options()
    options.addOption("l", "libs", true, "Libs path")
    options.addOption("p", "project", true, "Project path")
    options.addOption("i", "includes", true, "Classes or packages needs to be analyzed")
    options.addOption("s", "strategy", true, "Test suite generation strategy")

    val parser = DefaultParser()
    val cmd = parser.parse(options, args)

    log.info("===Start main function===")

    val exampleProjectDir = cmd.getOptionValue("project")
    val classesOrPackagesToAnalyze = cmd.getOptionValue("includes").split('&')
    val libsPath = cmd.getOptionValue("libs")
    val gaStrategy = cmd.getOptionValue("strategy")

    val client = Client(exampleProjectDir, classesOrPackagesToAnalyze, libsPath, gaStrategy)
    client.analyze()
    client.generateTestSuite()
}


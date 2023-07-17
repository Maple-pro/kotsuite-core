package org.kotsuite.client

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.Configs
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {

//    val log = LoggerFactory.getLogger("org.kotsuite.client.Main")
    val logger = LogManager.getLogger()

    // Deal with parameter options
    val options = Options()
    with(options) {
        addOption("p", "project", true, "Project path")
        addOption("m", "module", true, "Selected module path")
        addOption("cp", "classpath", true, "Selected module classpath")
        addOption("src", "source", true, "Selected module source root")
        addOption("i", "includes", true, "Include Rules, use & to joint multiple rules")
        addOption("l", "libs", true, "Libs path")
        addOption("s", "strategy", true, "Test suite generation strategy")
    }

    val parser = DefaultParser()
    val cmd = parser.parse(options, args)

    logger.log(Configs.sectionLevel, "[Start main function]")

    val projectPath = cmd.getOptionValue("project")
    val modulePath = cmd.getOptionValue("module")
    val moduleClassPath = cmd.getOptionValue("classpath")
    val moduleSourcePath = cmd.getOptionValue("source")
    val includeRules = cmd.getOptionValue("includes").split('&')
    val libsPath = cmd.getOptionValue("libs")
    val gaStrategy = cmd.getOptionValue("strategy")

    val client = Client(projectPath, modulePath, moduleClassPath, moduleSourcePath, includeRules, libsPath, gaStrategy)
    client.analyze(moduleClassPath)
    client.generateTestSuite()
}


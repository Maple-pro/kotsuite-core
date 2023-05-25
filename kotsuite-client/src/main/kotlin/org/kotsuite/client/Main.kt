package org.kotsuite.client

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("org.kotsuite.client.Main")

    // Deal with parameter options
    val options = Options()
    options.addOption("p", "project", true, "Project path")
    options.addOption("m", "module", true, "Selected module path")
    options.addOption("cp", "classpath", true, "Selected module classpath")
    options.addOption("src", "source", true, "Selected module source root")
    options.addOption("i", "includes", true, "Include Rules, use & to joint multiple rules")
    options.addOption("l", "libs", true, "Libs path")
    options.addOption("s", "strategy", true, "Test suite generation strategy")

    val parser = DefaultParser()
    val cmd = parser.parse(options, args)

    log.info("===[Start main function]===")

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


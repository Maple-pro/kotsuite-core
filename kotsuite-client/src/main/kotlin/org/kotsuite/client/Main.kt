package org.kotsuite.client

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import java.io.File

fun main(args: Array<String>) {
    // Deal with parameter options
    val options = Options()
    with(options) {
        addOption("p", "project", true, "Project path")
        addOption("m", "module", true, "Selected module path")
        addOption("cp", "classpath", true, "Selected module classpath")
        addOption("src", "source", true, "Selected module source root")
        addOption("i", "includes", true, "Include Rules, use & to joint multiple rules")
        addOption("l", "libs", true, "KotSuite libs path")
        addOption("s", "strategy", true, "Test suite generation strategy")
        addOption("d", "dependency", true, "Module dependencies class path roots")
//        addOption("ut", "unittest", true, "Unit test source path")
//        addOption("at", "androidtest", true, "Android test source path")
    }

    val parser = DefaultParser()
    val cmd = parser.parse(options, args)

    val projectPath = cmd.getOptionValue("project")
    val modulePath = cmd.getOptionValue("module")
    val moduleClassPath = cmd.getOptionValue("classpath")
    val moduleSourcePath = cmd.getOptionValue("source")
    val includeRules = cmd.getOptionValue("includes").split('&')
    val libsPath = cmd.getOptionValue("libs")
    val gaStrategy = cmd.getOptionValue("strategy")
    val dependencyClassPaths = cmd.getOptionValue("dependency")

    val logFilePath = cmd.getOptionValue("module") + File.separator + "kotsuite.log"
    System.setProperty("logFilePath", logFilePath)

    val log = LogManager.getLogger()

    log.log(Configs.sectionLevel, "[Start main function]")
    log.info("KotSuite Core Version: ${Configs.KOTSUITE_CORE_VERSION}")

    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        log.error("Algorithm Crash")
        log.error("Uncaught exception occurred: $throwable")
        log.error(throwable.stackTraceToString())
    }

    val client = Client(
        projectPath,
        modulePath,
        moduleClassPath,
        moduleSourcePath,
        includeRules,
        libsPath,
        gaStrategy,
        dependencyClassPaths,
    )
    client.analyze()
    client.generateTestSuite()
}


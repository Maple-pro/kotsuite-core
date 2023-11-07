package org.kotsuite.utils

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import org.kotsuite.Configs

fun Logger.logCommandOutput(psOutput: List<String>, psError: List<String>) {
    if (Configs.LOG_COMMAND_OUTPUT) {
        this.logLines(psOutput, Level.INFO)
    }
    this.logLines(psError, Level.ERROR)
}

fun Logger.logLines(lines: List<String>, level: Level) {
    lines.forEach {
        this.log(level, it)
    }
}

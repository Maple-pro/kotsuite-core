package org.kotsuite.ga.utils

import org.slf4j.Logger
import java.io.BufferedReader
import java.io.InputStreamReader

object LoggerUtils {
    fun logCommandOutput(log: Logger, ps: Process) {
        val stdInput = BufferedReader(InputStreamReader(ps.inputStream))
        val stdError = BufferedReader(InputStreamReader(ps.errorStream))

        log.info("Generating execution data file...")

        var s: String? = stdInput.readLine()
        while (s != null) {
            log.info(s)
            s = stdInput.readLine()
        }

        s = stdError.readLine()
        while (s != null) {
            log.error(s)
            s = stdError.readLine()
        }
    }
}
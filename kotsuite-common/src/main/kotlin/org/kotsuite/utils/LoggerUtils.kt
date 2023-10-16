package org.kotsuite.utils

import org.apache.logging.log4j.Logger
import org.kotsuite.Configs
import java.io.BufferedReader
import java.io.InputStreamReader

object LoggerUtils {
    fun Logger.logCommandOutput(ps: Process) {
        val stdInput = BufferedReader(InputStreamReader(ps.inputStream))
        val stdError = BufferedReader(InputStreamReader(ps.errorStream))

        var s: String?

        if (Configs.LOG_COMMAND_OUTPUT) {
            s = stdInput.readLine()
            while (s != null) {
                this.info(s)
                s = stdInput.readLine()
            }
        }

        s = stdError.readLine()
        while (s != null) {
            this.error(s)
            s = stdError.readLine()
        }
    }
}
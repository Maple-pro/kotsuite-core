package org.kotsuite.ga.utils

import java.util.UUID

object Utils {
    fun isLinux() = System.getProperty("os.name") == "Linux"
}
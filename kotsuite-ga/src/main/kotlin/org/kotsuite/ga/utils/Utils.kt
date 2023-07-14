package org.kotsuite.ga.utils

object Utils {
    fun isLinux() = System.getProperty("os.name") == "Linux"
}
package org.kotsuite.utils

import java.io.File

object FileUtils {
    fun isLinux() = System.getProperty("os.name") == "Linux"

    fun deleteDirectory(file: File) {
        if (!file.exists()) return

        if (file.isDirectory) {
            file.listFiles()?.forEach { deleteDirectory(it) }
        }

        file.delete()
    }
}
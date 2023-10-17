package org.kotsuite.utils

object IDUtils {
    private var id = 0

    fun getId(): Int {
        id++
        return id
    }
}
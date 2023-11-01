package org.kotsuite.utils

import soot.SootClass

object IDUtils {
    private var id = 0

    fun getId(): Int {
        id++
        return id
    }
}

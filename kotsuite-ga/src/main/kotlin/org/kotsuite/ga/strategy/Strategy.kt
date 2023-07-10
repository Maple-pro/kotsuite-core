package org.kotsuite.ga.strategy

import org.kotsuite.ga.solution.WholeSolution

interface Strategy {

    fun generateWholeSolution(): WholeSolution

}
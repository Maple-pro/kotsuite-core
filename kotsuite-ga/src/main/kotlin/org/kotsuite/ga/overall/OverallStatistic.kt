package org.kotsuite.ga.overall

import com.google.gson.GsonBuilder

data class OverallStatistic(
    val module: String,
    val classes: List<SimpleClassStatistic>,
) {
    override fun toString(): String {
        val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
        return gson.toJson(this)
    }
}

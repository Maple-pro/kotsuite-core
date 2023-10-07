package org.kotsuite.ga.chromosome

import com.google.gson.annotations.SerializedName

data class Assertion(
    @SerializedName("assert_type") val assertType: String,
    @SerializedName("assert_value") val assertValue: String,
    @SerializedName("method") val method: String,
    @SerializedName("class") val clazz: String,
)

package org.kotsuite.ga.commands

import org.apache.logging.log4j.LogManager

const val CLASS = "class"
const val METHOD = "method"

const val VALUE_SEPARATOR = ","

private val VALID_OPTIONS = listOf(
    CLASS,
    METHOD,
)

class KotMainCliOptions {

    private val log = LogManager.getLogger()

    private val options = HashMap<String, List<String>>()

    fun setClass(classes: List<String>) {
        options[CLASS] = classes
    }

    fun setMethod(method: String) {
        options[METHOD] = listOf(method)
    }

    fun getCliArguments(): List<String> {
        val arguments = ArrayList<String>()
        for (key in VALID_OPTIONS) {
            val value = options[key]
            if (value != null) {
                arguments.add("-$key")
                arguments.add(value.joinToString(VALUE_SEPARATOR))
            }
        }
        return arguments
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (key in VALID_OPTIONS) {
            val value = options[key]
            if (value != null) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(" ")
                }
                val valueStr = value.joinToString(VALUE_SEPARATOR)
                stringBuilder.append("-")
                stringBuilder.append(key)
                stringBuilder.append(" ")
                stringBuilder.append(valueStr)
            }
        }
        return stringBuilder.toString()
    }

}
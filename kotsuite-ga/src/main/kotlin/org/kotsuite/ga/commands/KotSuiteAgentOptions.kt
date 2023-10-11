package org.kotsuite.ga.commands

import org.apache.logging.log4j.LogManager
import java.io.File

const val COLLECT_ASSERT = "collectAssert"
const val OUTPUT_FILE = "outputFile"
const val TEST_CLASS = "testClass"
const val TEST_METHOD = "testMethod"
const val TARGET_CLASS = "targetClass"
const val TARGET_METHOD = "targetMethod"
const val TARGET_METHOD_DESC = "targetMethodDesc"

private val VALID_OPTIONS = listOf(
    COLLECT_ASSERT,
    OUTPUT_FILE,
    TEST_CLASS,
    TEST_METHOD,
    TARGET_CLASS,
    TARGET_METHOD,
    TARGET_METHOD_DESC,
)

class KotSuiteAgentOptions() {

    private val log = LogManager.getLogger()

    constructor(optionStr: String): this() {
        if (optionStr == "" || optionStr.isBlank()) {
            return
        }

        for (entry in optionStr.split(',')) {
            val parts = entry.split('=')
            if (parts.size != 2) {
                log.error("Invalid option: $entry")
                continue
            }

            val key = parts[0].trim()
            val value = parts[1].trim()
            if (!VALID_OPTIONS.contains(key)) {
                log.error("Invalid option: $key")
                continue
            }
            options[key] = value
        }
    }

    private val options = HashMap<String, String>()

    fun setCollectAssert(collectAssert: Boolean) {
        options[COLLECT_ASSERT] = if(collectAssert) {
            "true"
        } else {
            "false"
        }
    }

    fun setOutputFile(outputFile: String) {
        options[OUTPUT_FILE] = outputFile
    }

    fun setTestClass(testClass: String) {
        options[TEST_CLASS] = testClass
    }

    fun setTestMethod(testMethod: String) {
        options[TEST_METHOD] = testMethod
    }

    fun setTargetClass(targetClass: String) {
        options[TARGET_CLASS] = targetClass
    }

    fun setTargetMethod(targetMethod: String) {
        options[TARGET_METHOD] = targetMethod
    }

    fun setTargetMethodDesc(targetMethodDesc: String) {
        options[TARGET_METHOD_DESC] = targetMethodDesc
    }

    fun getVMArgument(kotsuiteAgentJarFile: File): String {
        return String.format("-javaagent:%s=%s", kotsuiteAgentJarFile, this)
    }

    fun getQuotedVMArgument(kotsuiteAgentJarFile: File): String {
        return "\"${getVMArgument(kotsuiteAgentJarFile)}\""
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (key in VALID_OPTIONS) {
            val value = options[key]
            if (value != null) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(',')
                }
                stringBuilder.append(key).append('=').append(value)
            }
        }
        return stringBuilder.toString()
    }
}

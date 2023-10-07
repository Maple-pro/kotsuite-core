package org.kotsuite.ga.chromosome

import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.coverage.fitness.Fitness
import soot.SootMethod
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class Population(
    val targetMethod: SootMethod,
    var round: Int,
    val testCases: List<TestCase>,
) {
    private val log = LogManager.getLogger()

    companion object {
        private val idGenerator = AtomicInteger(0)
    }

    private val id: Int = idGenerator.getAndIncrement()

    var fitness: Fitness? = null

    /**
     * TODO
     * Select test cases from old population to form a new population
     *
     * @return
     */
    fun select(): Population {
        return this
    }

    /**
     * TODO
     * Mutate
     *
     * @return
     */
    fun mutate(): Population {
        return this
    }

    /**
     * TODO
     * Crossover
     *
     * @return
     */
    fun crossover(): Population {
        return this
    }

    /**
     * Get population name,
     *
     * e.g., if the target method is `int foo(int)`, and round is 1,
     * then the population name is `assertions_org.example.MyClass_foo_int_int_round1_id1`
     *
     * @return
     */
    fun getPopulationAssertionName(): String {
        val parameterTypeString = targetMethod.parameterTypes.joinToString("_")
        return "assertions" +
                "_" + targetMethod.declaringClass.name +
                "_" + targetMethod.name +
                "_" + targetMethod.returnType +
                "_" + parameterTypeString +
                "_" + "round" + round +
                "_" + "id" + id
    }

    fun getPopulationClassName(): String {
        val targetClass = targetMethod.declaringClass
        val targetClassName = targetClass.shortName
        val capitalizedMethodName = targetMethod.name.replaceFirstChar { it.uppercase() }
        return "Temp$targetClassName${capitalizedMethodName}Round${round}ID$id"
    }

    fun addAssertions(assertFile: File) {
        val gson = Gson()
        val lines = mutableListOf<String>()
        val methodName2Assertion = mutableMapOf<String, Assertion>()
        try {
            // Read the file line by line
            assertFile.useLines { lines.addAll(it.toList()) }

            // Deserialize each line into a JSON object
            for (line in lines) {
                try {
                    val jsonObject = gson.fromJson(line, Assertion::class.java)
                    methodName2Assertion[jsonObject.method] = jsonObject
                } catch (e: Exception) {
                    log.error("Failed to deserialize JSON: $line")
                }
            }
        } catch (e: Exception) {
            log.error("An error occurred: ${e.message}")
        }

        testCases.forEach {
            val assertion = methodName2Assertion[it.testCaseName]
            if (assertion != null) {
                it.assertion = assertion
            }
        }
    }

    /**
     * TODO: Minimizer
     *
     */
    fun minimizer() {

    }
}
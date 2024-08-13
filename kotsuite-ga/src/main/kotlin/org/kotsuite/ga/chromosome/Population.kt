package org.kotsuite.ga.chromosome

import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.kotsuite.ga.coverage.fitness.Fitness
import soot.SootMethod
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/**
 * 遗传算法种群模型
 *
 * @param [targetMethod] 待测方法
 * @param [round] 当前代数
 * @param [testCases] 种群中的测试用例
 */
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
     * 生成种群中断言文件的文件名
     *
     * e.g., if the target method is `int foo(int)`, and round is 1,
     * then the assertion filename of the population name is `assertions_org.example.MyClass_foo_int_int_round1_id1`
     *
     * @return 种群中断言的名字
     */
    fun generatePopulationAssertionFileName(): String {
        val parameterTypeString = targetMethod.parameterTypes.joinToString("_")
        return "assertions" +
                "_" + targetMethod.declaringClass.name +
                "_" + targetMethod.name +
                "_" + targetMethod.returnType +
                "_" + parameterTypeString +
                "_" + "round" + round +
                "_" + "id" + id
    }

    /**
     * 生成种群对应的测试类的类名
     *
     * @return 种群对应的测试类的类名
     */
    fun generatePopulationClassName(): String {
        val targetClass = targetMethod.declaringClass
        val targetClassName = targetClass.shortName
        val capitalizedMethodName = targetMethod.name.replaceFirstChar { it.uppercase() }
        return "Temp$targetClassName${capitalizedMethodName}Round${round}ID$id"
    }

    /**
     * 向 assertion 文件中添加断言
     */
    fun appendAssertionsToFile(assertFile: File) {
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
     * 测试用例约简，获取达到最大覆盖率的最小测试用例集
     */
    fun minimizer(): Population {
        val testCaseCoverageHashCodes = testCases.map { it.coverageHashCodes }

        val distinctIndices = mutableListOf<Int>()
        val distinctItems = mutableSetOf<List<Int>>()

        for (i in testCaseCoverageHashCodes.indices) {
            val currentItem = testCaseCoverageHashCodes[i]
            if (distinctItems.add(currentItem)) {
                distinctIndices.add(i)
            }
        }

        val minimizedTestCases = distinctIndices.map { testCases[it] }

        log.info("Minimization: ${testCases.size} -> ${minimizedTestCases.size}")

        return Population(targetMethod, round, minimizedTestCases)
    }
}
package org.kotsuite.ga.strategy.standard

import org.kotsuite.ga.GAStrategy
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.chromosome.TestClass
import org.kotsuite.ga.coverage.fitness.Fitness
import org.kotsuite.ga.coverage.fitness.TestCaseFitness
import org.kotsuite.ga.coverage.fitness.TestSuiteFitness
import org.kotsuite.ga.strategy.random.RandomStrategy
import soot.SootMethod
import java.nio.file.Files
import java.nio.file.Paths

/**
 * testCaseGeneration(classUnderTest: Class)
 * ```
 * targetsToCover = targets(classUnderTest)
 * curPopulation = generateRandomPopulation(popSize)
 * while targetsToCover != null and executionTime() < maxExecutionTime
 *     t = selectTarget(targetsToCover), attempts = 0
 *     while not covered(t) and attempts < maxAttempts
 *         execute test cases in curPopulation
 *         update targetsToCover
 *         if covered(t) break
 *         compute fitness_t for test cases in curPopulation
 *         extract newPopulation from curPopulation according to fitness_t
 * 	       mutate newPopulation
 * 	       curPopulation = newPopulation
 * 	       attempts = attempts +1
 *     end while
 * end while
 * ```
 */
class StandardGAStrategy(
    private val maxAttempt: Int,
    private val projectDir: String,
    private val classesFilePath: String,
): GAStrategy() {

    private val populationOutputPath = "$projectDir/kotsuite/population"

    init {
        Files.createDirectories(Paths.get(populationOutputPath))
    }

    /**
     * Steps:
     * 1. Generate random test cases for methods: `RandomStrategy.generateTestCasesForMethod()` --> curPopulation
     * 2. Execute fitness value
     * 3. Meet the coverage criteria ? Output : Next attempt
     * 4. Extract newPopulation from curPopulation --> newPopulation
     * 5. Mutate population --> curPopulation
     * 6. Go to Step 2
     *
     * Variables:
     * - curPopulation
     * - newPopulation
     *
     * Actions:
     * - Generation random test cases
     * - Execute fitness value of each test case
     * - Meet the coverage criteria?
     * - Selection to generate newPopulation
     * - Mutate newPopulation to get curPopulation
     */
    override fun generateTestCasesForMethod(targetMethod: SootMethod): List<TestCase> {
        var curPopulation = RandomStrategy.generateTestCasesForMethod(targetMethod)
        var attempt = 0

        while (attempt <= maxAttempt) {
            // 1. get test suite coverage info
            val fitnessValue = TestSuiteFitness.generateTestSuiteFitness(curPopulation)

            // 2. meet the coverage criteria ? output : continue
            val isCoverTargets = isCoverTargets(fitnessValue)
            if (isCoverTargets) break

            // 3. get coverage info
            curPopulation.forEach {
                TestCaseFitness.generateTestCaseFitness(it, targetMethod, populationOutputPath, classesFilePath)
            }

            // 4. selection
            val newPopulation = selectNewPopulation(curPopulation)

            // 5. mutate
            curPopulation = mutatePopulation(newPopulation)

            attempt++
        }

        return curPopulation
    }

    private fun isCoverTargets(fitnessValue: Fitness): Boolean {
        TODO()
    }

    private fun selectNewPopulation(curPopulation: List<TestCase>): List<TestCase> {
        TODO()
    }

    private fun mutatePopulation(population: List<TestCase>): List<TestCase> {
        TODO()
    }

}
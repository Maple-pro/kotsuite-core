package org.kotsuite.ga.strategy.standard

import org.kotsuite.ga.Configs
import org.kotsuite.ga.chromosome.Population
import org.kotsuite.ga.strategy.Strategy
import org.kotsuite.ga.coverage.fitness.Fitness
import org.kotsuite.ga.coverage.fitness.PopulationFitness
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.ga.strategy.random.RandomStrategy
import soot.SootClass
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
object StandardGAStrategy: Strategy() {

    private val maxAttempt = Configs.maxAttempt
    private val moduleDir = Configs.modulePath
    private val classesFilePath = Configs.classesFilePath
    private val populationOutputPath = "$moduleDir/kotsuite/population"

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
     * - Mutate newPopulation
     * - Crossover newPopulation
     */
    override fun generateMethodSolution(targetMethod: SootMethod, targetClass: SootClass): MethodSolution {
        val initialTestCases = RandomStrategy.generateTestCasesForMethod(targetMethod)
        var curPopulation = Population(targetMethod, 0, initialTestCases)
        var round = 0

        while(round <= maxAttempt) {
            // 1. get test suite coverage info
            val fitness = PopulationFitness.generatePopulationFitness(curPopulation)

            // 2. meet the coverage criteria ? output : continue
            val isCoverTargets = isCoverTargets(fitness)
            if (isCoverTargets) break

            // 3. selection
            curPopulation = curPopulation.select()

            // 4. mutate
            curPopulation = curPopulation.mutate()

            // 5. crossover
            curPopulation = curPopulation.crossover()

            round++
        }

        return MethodSolution(targetMethod, curPopulation.testCases)
    }

    private fun isCoverTargets(fitness: Fitness): Boolean {
        return fitness.lineCoverage >= Configs.targetLineCoverage
                && fitness.ccCoverage >= Configs.targetCCCoverage
    }
}
package org.kotsuite.ga.strategy.standard

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import org.kotsuite.ga.chromosome.Population
import org.kotsuite.ga.strategy.Strategy
import org.kotsuite.ga.coverage.fitness.Fitness
import org.kotsuite.ga.coverage.fitness.PopulationFitness
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.ga.strategy.random.RandomStrategy
import soot.SootClass
import soot.SootMethod

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

    private val log = LogManager.getLogger()

//    private const val maxAttempt = Configs.maxAttempt
    private const val MAX_ATTEMPT = 1  // [test] for test only

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
        log.log(Configs.sectionLevel, "[Class: $targetClass, Method: $targetMethod]")

        log.log(Level.INFO, "Generate initial population")

        val initialTestCases = RandomStrategy.generateTestCasesForMethod(targetMethod)
        var curPopulation = Population(targetMethod, 0, initialTestCases)
        var round = 0

        while(true) {
            log.log(Configs.sectionLevel, "[Round $round]")

            // 1. get test suite coverage info
            val fitness = PopulationFitness.generatePopulationFitness(curPopulation) ?: break

            // 2. meet the coverage criteria ? output : continue
            log.log(Level.INFO, "Fitness: $fitness")
            val isCoverTargets = isCoverTargets(fitness)
            if (isCoverTargets) break

            round++
            if (round > MAX_ATTEMPT) break

            // 3. select, mutate and crossover
            log.log(Level.INFO, "Select, mutate and crossover")
            curPopulation = curPopulation.select().mutate().crossover()

            curPopulation.round = round
        }

        // TODO: remove duplicate test cases

        return MethodSolution(targetMethod, curPopulation.testCases)
    }

    private fun isCoverTargets(fitness: Fitness): Boolean {
        return fitness.lineCoverage >= Configs.TARGET_LINE_COVERAGE
                && fitness.ccCoverage >= Configs.TARGET_CC_COVERAGE
    }

    @Override
    override fun toString() = "Standard GA Strategy"
}
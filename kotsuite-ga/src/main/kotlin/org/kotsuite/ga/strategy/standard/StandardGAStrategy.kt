package org.kotsuite.ga.strategy.standard

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.kotsuite.Configs
import org.kotsuite.ga.assertion.AssertionGenerator
import org.kotsuite.ga.chromosome.Population
import org.kotsuite.ga.strategy.Strategy
import org.kotsuite.ga.coverage.fitness.Fitness
import org.kotsuite.ga.coverage.fitness.PopulationFitness
import org.kotsuite.ga.solution.MethodSolution
import org.kotsuite.ga.strategy.random.RandomStrategy
import soot.SootClass
import soot.SootMethod
import java.io.File

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

    private const val MAX_ATTEMPT = Configs.MAX_ATTEMPT

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

            // Get test suite coverage info
            log.debug("Generating population coverage info...")
            val assertFileName = curPopulation.getPopulationAssertionName() + ".txt"
            val assertFilePath = Configs.assertOutputPath + File.separatorChar + assertFileName
            PopulationFitness.generatePopulationFitness(curPopulation, assertFilePath)

            // Generate assertion for each testcase
            log.debug("Adding assertion to each testcase...")
            AssertionGenerator.addAssertions(curPopulation, File(assertFilePath))

            // Meet the coverage criteria ? output : continue
            log.debug("Check whether the fitness of the population meets the requirements...")
            val fitness = curPopulation.fitness ?: Fitness(0.0, 0.0)
            log.log(Level.INFO, "Fitness: $fitness")
            val isCoverTargets = isCoverTargets(fitness)
            if (isCoverTargets) break

            round++
            if (round >= MAX_ATTEMPT) break

            // Select, mutate and crossover
            log.debug("Select, mutate and crossover...")
            curPopulation = curPopulation.select().mutate().crossover()

            curPopulation.round = round
        }

        // Remove duplicated test cases
//        curPopulation.minimizer()

        return MethodSolution(targetMethod, curPopulation.testCases)
    }

    private fun isCoverTargets(fitness: Fitness): Boolean {
        return fitness.lineCoverage >= Configs.TARGET_LINE_COVERAGE
                && fitness.ccCoverage >= Configs.TARGET_CC_COVERAGE
    }

    @Override
    override fun toString() = "Standard GA Strategy"
}
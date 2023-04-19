package org.kotsuite.ga.strategy.standard

import org.kotsuite.ga.GAStrategy
import org.kotsuite.ga.chromosome.TestCase
import org.kotsuite.ga.strategy.random.RandomStrategy
import soot.SootMethod

/**
 * testCaseGeneration(classUnderTest: Class)
 * 1 targetsToCover = targets(classUnderTest)
 * 2 curPopulation = generateRandomPopulation(popSize)
 * 3 while targetsToCover != null and executionTime() < maxExecutionTime
 * 4    t = selectTarget(targetsToCover), attempts = 0
 * 5 	while not covered(t) and attempts < maxAttempts
 * 6 		execute test cases in curPopulation
 * 7 		update targetsToCover
 * 8 		if covered(t) break
 * 9 		compute fitness_t for test cases in curPopulation
 * 10 		extract newPopulation from curPopulation according to fitness_t
 * 11 		mutate newPopulation
 * 12 		curPopulation = newPopulation
 * 13 		attempts = attempts +1
 * 14 	end while
 * 15 end while
 */
object StandardGAStrategy: GAStrategy() {

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

        TODO("Not yet implemented")
    }

    fun generateTestCaseForMethod(targetMethod: SootMethod, testCaseName: String): TestCase {
        TODO("Not yet implemented")
    }
}
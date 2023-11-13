package org.kotsuite.ga.report

import com.google.gson.GsonBuilder
import org.kotsuite.ga.solution.ClassSolution
import org.kotsuite.ga.solution.WholeSolution
import soot.SootClass

data class Report(
    val classInfos: List<ClassInfo>
) {
    companion object {
        /**
         * @param wholeSolution 只包含过滤后了的能生成测试用例的类（至少包含一个待测函数）
         * @param allClasses 符合 includeRules 的所有的类
         */
        fun fromWholeSolution(wholeSolution: WholeSolution, allClasses: List<SootClass>): Report {
            val className2Solution = wholeSolution.classSolutions.associateBy { it.targetClass.name }

            val classInfos = allClasses.map {
                val className = it.name
                val classType = it.getClassType()
                val classReason = it.getClassReason()
                var classCoverageInfo = CoverageInfo(0.0, 0.0)

                var methodInfos = emptyList<MethodInfo>()
                if (classType == ClassType.ENABLE) { // 获取 methodInfos
                    var classSolution: ClassSolution? = null
                    if (className2Solution.containsKey(className)) { // 获取每个 method 中的 solution 信息
                        classSolution = className2Solution[className]!!
                    }

                    if (classSolution != null) {
                        classCoverageInfo = CoverageInfo.fromFitness(classSolution.fitness)
                    }

                    methodInfos = it.methods.map { method ->
                        val methodSig = method.signature
                        val methodType = method.getMethodType()
                        val methodReason = method.getMethodReason()

                        var testCaseInfos = emptyList<TestCaseInfo>()
                        var coverageInfo = CoverageInfo(0.0, 0.0)
                        if (classSolution != null) { // 获取每个 test case 中的 solution 信息
                            val methodSolution = classSolution.methodSolutions.find { methodSolution ->
                                methodSolution.targetMethod.signature == methodSig
                            }
                            if (methodSolution != null) {
                                testCaseInfos = methodSolution.testCases.map { testCase ->
                                    val testCaseName = testCase.testCaseName
                                    val testReason = testCase.testResult
                                    val testCaseCoverageInfo = CoverageInfo.fromFitness(testCase.fitness)
                                    TestCaseInfo(testCaseName, testReason, testCaseCoverageInfo)
                                }

                                coverageInfo = CoverageInfo.fromFitness(methodSolution.fitness)
                            }
                        }

                        MethodInfo(methodSig, methodType, methodReason, coverageInfo, testCaseInfos)
                    }
                }

                ClassInfo(className, classType, classReason, classCoverageInfo, methodInfos)
            }

            return Report(classInfos)
        }
    }

    override fun toString(): String {
        val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
        return gson.toJson(this)
    }
}


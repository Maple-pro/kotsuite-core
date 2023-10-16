package org.kotsuite.ga.chromosome.action

enum class InitializationType {
    CONSTRUCTOR, // 使用构造函数初始化对象
    MOCK, // 使用 mock 初始化对象
    SPY, // 使用 spy 初始化对象
}
package org.kotsuite.ga.chromosome.action

enum class InitializationType {
    CONSTRUCTOR, // 使用构造函数初始化对象
    TEST_DOUBLE, // 使用测试替身初始化对象
    INSTANCE, // 是 Object 类，直接使用 INSTANCE 字段初始化对象
}

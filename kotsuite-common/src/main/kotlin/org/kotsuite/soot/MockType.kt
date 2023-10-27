package org.kotsuite.soot

enum class TestDoubleType {
    MOCKITO_MOCK, // 使用 Mockito mock 初始化对象
    MOCKITO_SPY, // 使用 Mockito spy 初始化对象
    JMOCKK_MOCK, // 使用 Mockk mock 初始化对象
    JMOCKK_SPY, // 使用 Mockk spy 初始化对象
}

enum class MockWhenActionType {
    MOCKITO,
    JMOCKK,
}

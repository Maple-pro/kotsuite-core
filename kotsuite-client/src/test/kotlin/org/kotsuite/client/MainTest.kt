package org.kotsuite.client

import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun testMain() {
        val myApplicationPath = "../data/MyApplication"
        val args = arrayOf(myApplicationPath)
        main(args)
    }
}
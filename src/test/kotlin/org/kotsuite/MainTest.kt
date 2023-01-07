package org.kotsuite

import org.junit.jupiter.api.Test

class MainTest {
    @Test fun testMain() {
        val args = arrayOf("param1", "param2")
        main(args)
    }
}
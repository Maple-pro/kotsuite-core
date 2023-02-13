package org.kotsuite.client

import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun testMain() {
        val args = arrayOf("/path/to/bytecodes/")
        main(args)
    }
}
package org.kotsuite.client

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        return
    }

    val dataDir = args[0]
    val client = Client(dataDir)
    client.analysis()
    client.generateTestSuite()
}


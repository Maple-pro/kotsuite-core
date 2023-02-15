package org.kotsuite.client

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println("Error: no project path.")
    }

    val projectPath = args[0]
    val client = Client()
    client.analysis()
    client.generateTestSuite()
}

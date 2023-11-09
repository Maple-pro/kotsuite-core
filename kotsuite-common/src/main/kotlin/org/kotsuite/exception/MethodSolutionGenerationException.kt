package org.kotsuite.exception

/**
 * Method solution generation exception.
 *
 * When this exception occurred, it should end the process of the current method solution generation.
 */
open class MethodSolutionGenerationException : ClassSolutionGenerationException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
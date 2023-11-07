package org.kotsuite.exception

/**
 * Class solution generation exception.
 *
 * When this exception occurred, it should end the process of the current class solution generation.
 */
class ClassSolutionGenerationException : KotSuiteException {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
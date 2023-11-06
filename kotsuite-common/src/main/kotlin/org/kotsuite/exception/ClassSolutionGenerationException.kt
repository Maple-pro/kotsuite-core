package org.kotsuite.exception

/**
 * Class solution generation exception.
 *
 * When this exception occurred, it should end the process of the current class solution generation.
 *
 * @param message
 */
class ClassSolutionGenerationException(message: String) : Exception(message)
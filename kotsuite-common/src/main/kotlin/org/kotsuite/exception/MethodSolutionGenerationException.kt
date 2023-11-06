package org.kotsuite.exception

/**
 * Method solution generation exception.
 *
 * When this exception occurred, it should end the process of the current method solution generation.
 *
 * @param message
 */
class MethodSolutionGenerationException(message: String) : Exception(message)
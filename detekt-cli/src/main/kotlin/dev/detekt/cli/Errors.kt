package dev.detekt.cli

/**
 * Thrown when --help is requested.
 */
class HelpRequest(val usageText: String) : RuntimeException()

/**
 * Thrown when parsing arguments by JCommander and validating them further.
 * As the parsing logic prints the --help message along other parameter errors,
 * this exception signals the main loop that no further printing is necessary.
 */
class HandledArgumentViolation(message: String?, val usageText: String) : RuntimeException(message)

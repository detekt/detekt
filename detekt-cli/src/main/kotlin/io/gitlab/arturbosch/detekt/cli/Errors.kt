package io.gitlab.arturbosch.detekt.cli

/**
 * Thrown when --help is requested.
 */
class HelpRequest : RuntimeException()

/**
 * Thrown when parsing arguments by JCommander and validating them further.
 * As the parsing logic prints the --help message along other parameter errors,
 * this exception signals the main loop that no further printing is necessary.
 */
class HandledArgumentViolation : RuntimeException()

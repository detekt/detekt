package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.test.utils.NullPrintStream

/**
 * Creates an instance of [CliArgs]. Verification if the settings are sound
 * must be made by the caller.
 */
fun createCliArgs(vararg args: String): CliArgs {
    return parseArguments(args, NullPrintStream(), NullPrintStream())
}

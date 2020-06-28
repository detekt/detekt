package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.test.utils.NullPrintStream
import io.gitlab.arturbosch.detekt.cli.runners.Runner

/**
 * Creates an instance of [CliArgs]. Verification if the settings are sound
 * must be made by the caller.
 */
fun createCliArgs(vararg args: String): CliArgs {
    return parseArguments(args, NullPrintStream(), NullPrintStream())
}

fun createRunner(cliArgs: CliArgs): Runner = Runner(cliArgs, NullPrintStream(), NullPrintStream())

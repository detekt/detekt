package io.gitlab.arturbosch.detekt.cli

import dev.detekt.test.utils.NullPrintStream
import io.gitlab.arturbosch.detekt.cli.runners.Runner

fun CliArgs.toSpec() = createSpec(NullPrintStream(), NullPrintStream())

fun createRunner(cliArgs: CliArgs): Runner = Runner(cliArgs, NullPrintStream(), NullPrintStream())

fun executeDetekt(vararg args: String) {
    val cli = parseArguments(args)
    createRunner(cli).execute()
}

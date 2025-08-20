package dev.detekt.cli

import dev.detekt.cli.runners.Runner
import dev.detekt.test.utils.NullPrintStream

fun CliArgs.toSpec() = createSpec(NullPrintStream(), NullPrintStream())

fun createRunner(cliArgs: CliArgs): Runner = Runner(cliArgs, NullPrintStream(), NullPrintStream())

fun executeDetekt(vararg args: String) {
    val cli = parseArguments(args)
    createRunner(cli).execute()
}

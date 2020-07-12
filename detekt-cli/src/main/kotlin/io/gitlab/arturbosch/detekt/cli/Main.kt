@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.MaxIssuesReached
import io.github.detekt.tooling.api.UnexpectedError
import io.github.detekt.tooling.api.exitCode
import io.github.detekt.tooling.internal.NotApiButProbablyUsedByUsers
import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Executable
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.VersionPrinter
import java.io.PrintStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val result = CliRunner().run(args)
    when (val error = result.error) {
        is InvalidConfig, is MaxIssuesReached -> println(error.message)
        is UnexpectedError -> {
            when (val cause = error.cause) {
                is HelpRequest -> {
                    println(cause.usageText)
                    exitProcess(0)
                }
                is HandledArgumentViolation -> {
                    println(cause.message)
                    println(cause.usageText)
                }
                else -> cause.printStackTrace()
            }
        }
    }
    exitProcess(result.exitCode())
}

@NotApiButProbablyUsedByUsers
@Deprecated(
    "Don't build a runner yourself.",
    ReplaceWith(
        "DetektCli.load().run(args, outputPrinter, errorPrinter)",
        "io.github.detekt.tooling.api.DetektCli"
    )
)
fun buildRunner(
    args: Array<String>,
    outputPrinter: PrintStream,
    errorPrinter: PrintStream
): Executable {
    val arguments = parseArguments(args)
    return when {
        arguments.showVersion -> VersionPrinter(outputPrinter)
        arguments.generateConfig -> ConfigExporter(arguments, outputPrinter)
        arguments.printAst -> AstPrinter(arguments, outputPrinter)
        else -> Runner(arguments, outputPrinter, errorPrinter)
    }
}

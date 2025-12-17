@file:JvmName("Main")

package dev.detekt.cli

import dev.detekt.api.internal.whichKotlin
import dev.detekt.cli.runners.ConfigExporter
import dev.detekt.cli.runners.Executable
import dev.detekt.cli.runners.Runner
import dev.detekt.cli.runners.VersionPrinter
import dev.detekt.tooling.api.AnalysisResult
import dev.detekt.tooling.api.InvalidConfig
import dev.detekt.tooling.api.IssuesFound
import dev.detekt.tooling.api.UnexpectedError
import dev.detekt.tooling.internal.NotApiButProbablyUsedByUsers
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import java.io.PrintStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val result = CliRunner().run(args)
    @Suppress("ForbiddenMethodCall")
    when (val error = result.error) {
        is InvalidConfig, is IssuesFound -> println(error.message)
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
        else -> Unit // print nothing extra when there is no error
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
fun buildRunner(args: Array<String>, outputPrinter: PrintStream, errorPrinter: PrintStream): Executable {
    check(KotlinCompilerVersion.VERSION == whichKotlin()) {
        """
            detekt was compiled with Kotlin ${whichKotlin()} but is currently running with ${KotlinCompilerVersion.VERSION}.
            This is not supported. See https://detekt.dev/docs/gettingstarted/gradle#dependencies for more information.
        """.trimIndent()
    }
    val arguments = parseArguments(args)
    return when {
        arguments.showVersion -> VersionPrinter(outputPrinter)
        arguments.generateConfig != null -> ConfigExporter(arguments, outputPrinter)
        else -> Runner(arguments, outputPrinter, errorPrinter)
    }
}

@Suppress("detekt.MagicNumber")
internal fun AnalysisResult.exitCode(): Int =
    when (error) {
        is UnexpectedError -> 1
        is IssuesFound -> 2
        is InvalidConfig -> 3
        null -> 0
    }

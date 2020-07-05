@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.MaxIssuesReached
import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Executable
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.VersionPrinter
import io.gitlab.arturbosch.detekt.core.NotApiButProbablyUsedByUsers
import java.io.PrintStream
import kotlin.system.exitProcess

@Suppress("TooGenericExceptionCaught")
fun main(args: Array<String>) {
    try {
        buildRunner(args, System.out, System.err).execute()
    } catch (e: HelpRequest) {
        println(e.usageText)
    } catch (e: InvalidConfig) {
        println(e.message)
        exitProcess(ExitCode.INVALID_CONFIG.number)
    } catch (e: MaxIssuesReached) {
        println(e.message)
        exitProcess(ExitCode.MAX_ISSUES_REACHED.number)
    } catch (e: HandledArgumentViolation) {
        println(e.message)
        println(e.usageText)
        exitProcess(ExitCode.UNEXPECTED_DETEKT_ERROR.number)
    } catch (e: Exception) {
        e.printStackTrace()
        exitProcess(ExitCode.UNEXPECTED_DETEKT_ERROR.number)
    }
    exitProcess(ExitCode.NORMAL_RUN.number)
}

@NotApiButProbablyUsedByUsers
fun buildRunner(
    args: Array<String>,
    outputPrinter: PrintStream,
    errorPrinter: PrintStream,
): Executable {
    val arguments = parseArguments(args)
    return when {
        arguments.showVersion -> VersionPrinter(outputPrinter)
        arguments.generateConfig -> ConfigExporter(arguments, outputPrinter)
        arguments.printAst -> AstPrinter(arguments, outputPrinter)
        else -> Runner(arguments, outputPrinter, errorPrinter)
    }
}

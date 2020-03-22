@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.config.InvalidConfig
import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Executable
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.SingleRuleRunner
import io.gitlab.arturbosch.detekt.cli.runners.VersionPrinter
import io.gitlab.arturbosch.detekt.core.exists
import io.gitlab.arturbosch.detekt.core.isFile
import java.io.PrintStream
import kotlin.system.exitProcess

@Suppress("TooGenericExceptionCaught")
fun main(args: Array<String>) {
    try {
        buildRunner(args, System.out, System.err).execute()
    } catch (_: HelpRequest) {
        // handled by JCommander, exit normally
    } catch (e: InvalidConfig) {
        println(e.message)
        exitProcess(ExitCode.INVALID_CONFIG.number)
    } catch (e: BuildFailure) {
        println(e.message)
        exitProcess(ExitCode.MAX_ISSUES_REACHED.number)
    } catch (e: HandledArgumentViolation) {
        // messages are handled when parsing arguments
        exitProcess(ExitCode.UNEXPECTED_DETEKT_ERROR.number)
    } catch (e: Exception) {
        e.printStackTrace()
        exitProcess(ExitCode.UNEXPECTED_DETEKT_ERROR.number)
    }
    exitProcess(ExitCode.NORMAL_RUN.number)
}

fun buildRunner(
    args: Array<String>,
    outputPrinter: PrintStream,
    errorPrinter: PrintStream
): Executable {
    val arguments = parseArguments<CliArgs>(
        args,
        outputPrinter,
        errorPrinter
    ) { messages ->
        val baseline = baseline
        if (createBaseline && baseline == null) {
            messages += "Creating a baseline.xml requires the --baseline parameter to specify a path."
        }
        if (!createBaseline && baseline != null) {
            if (!baseline.exists()) {
                messages += "The file specified by --baseline should exist '$baseline'."
            } else if (!baseline.isFile()) {
                messages += "The path specified by --baseline should be a file '$baseline'."
            }
        }
    }
    return when {
        arguments.showVersion -> VersionPrinter(outputPrinter)
        arguments.generateConfig -> ConfigExporter(arguments)
        arguments.runRule != null -> SingleRuleRunner(arguments, outputPrinter, errorPrinter)
        arguments.printAst -> AstPrinter(arguments, outputPrinter)
        else -> Runner(arguments, outputPrinter, errorPrinter)
    }
}

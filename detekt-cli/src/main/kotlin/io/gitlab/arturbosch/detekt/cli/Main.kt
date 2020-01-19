@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Executable
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.SingleRuleRunner
import java.io.PrintStream
import kotlin.system.exitProcess

@Suppress("TooGenericExceptionCaught")
fun main(args: Array<String>) {
    try {
        buildRunner(args).execute()
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
    outputPrinter: PrintStream = System.out,
    errorPrinter: PrintStream = System.err
): Executable {
    val arguments = parseArguments<CliArgs>(
        args,
        outputPrinter,
        errorPrinter
    ) { messages ->
        if (createBaseline && baseline == null) {
            messages += "Creating a baseline.xml requires the --baseline parameter to specify a path."
        }
    }
    return when {
        arguments.generateConfig -> ConfigExporter(arguments)
        arguments.runRule != null -> SingleRuleRunner(arguments)
        arguments.printAst -> AstPrinter(arguments)
        else -> Runner(arguments, outputPrinter, errorPrinter)
    }
}

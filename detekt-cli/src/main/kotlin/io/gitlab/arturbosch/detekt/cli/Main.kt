@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Executable
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.SingleRuleRunner
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty
import kotlin.system.exitProcess

@Suppress("TooGenericExceptionCaught")
fun main(args: Array<String>) {
    try {
        buildRunner(args).execute()
    } catch(e: InvalidConfig) {
        // Exit with status code 3 when some properties in the configuration file are not expected to exist.
        e.messages.forEach(::println)
        exitProcess(3)
    } catch (e: BuildFailure) {
        // Exit with status code 2 when maxIssues value from configuration was reached.
        e.printStackTrace()
        exitProcess(2)
    } catch (e: Exception) {
        // Exit with status code 1 when an unexpected error occurred.
        e.printStackTrace()
        exitProcess(1)
    }
    // Exit with status code 0 when detekt ran normally and maxIssues or failThreshold count was not reached in
    // BuildFailureReport.
    exitProcess(0)
}

fun buildRunner(args: Array<String>): Executable {
    val arguments = parseArguments(args)
    return when {
        arguments.generateConfig -> ConfigExporter(arguments)
        arguments.runRule != null -> SingleRuleRunner(arguments)
        arguments.printAst -> AstPrinter(arguments)
        else -> Runner(arguments)
    }
}

private fun parseArguments(args: Array<String>): CliArgs {
    val (arguments, jcommander) = parseArguments<CliArgs>(args)
    val messages = validateCli(arguments)
    messages.ifNotEmpty {
        jcommander.failWithErrorMessages(messages)
    }
    return arguments
}

private fun validateCli(arguments: CliArgs): List<String> {
    val violations = ArrayList<String>()
    with(arguments) {
        if (createBaseline && baseline == null) {
            violations += "Creating a baseline.xml requires the --baseline parameter to specify a path."
        }
    }
    return violations
}

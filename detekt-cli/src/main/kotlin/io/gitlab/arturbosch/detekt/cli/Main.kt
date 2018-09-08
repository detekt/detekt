@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.SingleRuleRunner
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
fun main(args: Array<String>) {
	val arguments = parseArgumentsCheckingReportDirectory(args)
	LOG.active = arguments.debug
	val executable = when {
		arguments.generateConfig -> ConfigExporter()
		arguments.runRule != null -> SingleRuleRunner(arguments)
		arguments.printAst -> AstPrinter(arguments)
		else -> Runner(arguments)
	}
	executable.execute()
}

private fun parseArgumentsCheckingReportDirectory(args: Array<String>): CliArgs {
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

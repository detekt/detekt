@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.cli

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
		else -> Runner(arguments)
	}
	executable.execute()
}

private fun parseArgumentsCheckingReportDirectory(args: Array<String>): Args {
	val arguments = parseArguments(args)
	val messages = validateCli(arguments)
	messages.ifNotEmpty {
		failWithErrorMessages(messages)
	}
	return arguments
}

private fun validateCli(arguments: Args): List<String> {
	val violations = ArrayList<String>()
	with(arguments) {
		if (createBaseline && baseline == null) {
			violations += "Creating a baseline.xml requires the --baseline parameter to specify a path."
		}
	}
	return violations
}

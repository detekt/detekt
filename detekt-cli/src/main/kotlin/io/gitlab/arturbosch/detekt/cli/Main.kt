@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.core.isDirectory
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty
import java.nio.file.Files

/**
 * @author Artur Bosch
 */
fun main(args: Array<String>) {
	val arguments = parseArgumentsCheckingReportDirectory(args)
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
		output?.let {
			if (Files.exists(it) && it.isDirectory()) {
				violations += "Output file must not be a directory."
			}
		}
		if (createBaseline && baseline == null) {
			violations += "Creating a baseline.xml requires the --baseline parameter to specify a path."
		}
	}
	return violations
}

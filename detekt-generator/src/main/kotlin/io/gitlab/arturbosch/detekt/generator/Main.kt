@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.cli.failWithErrorMessages
import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.core.isFile
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty
import java.nio.file.Files

/**
 * @author Marvin Ramin
 * @author Artur Bosch
 */
fun main(args: Array<String>) {
	val arguments = parseArgumentsCheckingReportDirectory(args)
	val executable = Runner(arguments)
	executable.execute()
}

private fun parseArgumentsCheckingReportDirectory(args: Array<String>): GeneratorArgs {
	val arguments = parseArguments<GeneratorArgs>(args)
	val messages = validateCli(arguments)
	messages.ifNotEmpty {
		failWithErrorMessages(messages)
	}
	return arguments
}

private fun validateCli(arguments: GeneratorArgs): List<String> {
	val violations = ArrayList<String>()
	with(arguments) {
		if (Files.exists(documentationPath) && documentationPath.isFile()) {
			violations += "Documentation path must be a directory."
		}

		if (Files.exists(configPath) && configPath.isFile()) {
			violations += "Config path must be a directory."
		}
		// input paths are validated by MultipleExistingPathConverter
	}
	return violations
}

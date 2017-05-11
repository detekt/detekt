package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException

/**
 * @author Artur Bosch
 */
private val jCommander = JCommander()

fun parseArguments(args: Array<String>): Main {
	val cli = Main()
	jCommander.addObject(cli)
	jCommander.setProgramName("detekt")

	try {
		jCommander.parse(*args)
	} catch (ex: ParameterException) {
		val message = ex.message
		failWithErrorMessage(message)
	}

	if (cli.help) {
		jCommander.usage()
		System.exit(-1)
	}

	return cli
}

fun failWithErrorMessage(message: String?) {
	println(message)
	println()
	jCommander.usage()
	System.exit(-1)
}
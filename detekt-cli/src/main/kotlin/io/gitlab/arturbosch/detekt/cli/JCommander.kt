package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException

private val jCommander = JCommander()

fun parseArguments(args: Array<String>): Main {
	val cli = Main()
	jCommander.addObject(cli)
	jCommander.setProgramName("detekt")

	try {
		jCommander.parse(*args)
	} catch (ex: ParameterException) {
		val message = ex.message
		failWithErrorMessages(message)
	}

	if (cli.help) {
		jCommander.usage()
		System.exit(-1)
	}

	return cli
}

fun failWithErrorMessages(vararg messages: String?) {
	failWithErrorMessages(messages.asIterable())
}

fun failWithErrorMessages(messages: Iterable<String?>) {
	messages.forEach {
		println(it)
	}
	println()
	jCommander.usage()
	System.exit(-1)
}

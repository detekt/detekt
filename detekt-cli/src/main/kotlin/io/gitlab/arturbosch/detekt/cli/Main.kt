package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.core.Detekt
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
fun main(args: Array<String>) {
	val params = validateArguments(args)
	val results = Detekt(params.path, pathFilters = params.filters).run()
	printFindings(results)
}

data class Params(val path: Path, val filters: List<String>)

private fun validateArguments(args: Array<String>): Params {
	if (args.size == 0)
		throw IllegalArgumentException("You have to specify a project path as minimal configuration")

	val project = Paths.get(args[0])
	if (Files.notExists(project))
		throw IllegalArgumentException("Provided project path does not exist!")

	val filters = if (args.size == 2) args[1].split("[,.;:]") else listOf()

	return Params(project, filters)
}

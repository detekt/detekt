package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.test.resource
import java.nio.file.Files
import java.nio.file.Paths

internal fun readResource(filename: String): String {
	val path = Paths.get(resource(filename))
	return Files.readAllLines(path).joinToString("\n").trimEnd()
}

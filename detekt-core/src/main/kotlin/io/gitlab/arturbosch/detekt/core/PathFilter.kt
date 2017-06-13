package io.gitlab.arturbosch.detekt.core

import java.nio.file.Path
import java.util.regex.PatternSyntaxException

/**
 * @author Artur Bosch
 */
class PathFilter(pattern: String) {

	private val regex: Regex

	init {
		try {
			regex = Regex(pattern)
		} catch (exception: PatternSyntaxException) {
			throw IllegalArgumentException("Provided regex is not valid: $pattern")
		}
	}

	fun matches(path: Path): Boolean = path.toAbsolutePath().toString().matches(regex)
}
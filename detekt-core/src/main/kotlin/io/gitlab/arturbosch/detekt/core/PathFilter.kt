package io.gitlab.arturbosch.detekt.core

import java.nio.file.Path
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * @author Artur Bosch
 */
class PathFilter(val regex: String) {

	init {
		try {
			Pattern.compile(regex)
		} catch (exception: PatternSyntaxException) {
			throw IllegalArgumentException("Provided regex is not valid: $regex")
		}
	}

	fun matches(path: Path): Boolean {
		return path.toAbsolutePath().toString().matches(Regex(regex))
	}
}
package io.gitlab.arturbosch.detekt.core

import java.nio.file.Path
import java.util.regex.PatternSyntaxException

/**
 * @author Artur Bosch
 */
class PathFilter(pattern: String) {

	companion object {
		val IS_WINDOWS = System.getProperty("os.name").contains("Windows")
	}

	private val regex: Regex

	init {
		try {
			val independentPattern = if (IS_WINDOWS) pattern.replace("/", "\\\\") else pattern
			regex = Regex(independentPattern)
		} catch (exception: PatternSyntaxException) {
			throw IllegalArgumentException("Provided regex is not valid: $pattern")
		}
	}

	fun matches(path: Path): Boolean = path.toAbsolutePath().toString().matches(regex)
}

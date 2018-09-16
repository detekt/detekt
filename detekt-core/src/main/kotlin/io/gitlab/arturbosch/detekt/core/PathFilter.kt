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

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is PathFilter) return false

		// We compare the patterns — there is no meaningful way to compare equality for regexes,
		// but we control the creation of the regexes and we can say that all else is equal, so
		// that's good enough in this case.
		if (regex.pattern != other.regex.pattern) return false

		return true
	}

	override fun hashCode(): Int {
		return regex.hashCode()
	}

	override fun toString(): String {
		return "PathFilter(regex=$regex)"
	}
}

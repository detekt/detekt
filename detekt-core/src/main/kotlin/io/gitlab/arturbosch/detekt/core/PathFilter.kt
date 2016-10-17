package io.gitlab.arturbosch.detekt.core

import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class PathFilter(val regex: String) {
	fun matches(path: Path): Boolean {
		return path.toAbsolutePath().toString().matches(Regex(regex))
	}
}
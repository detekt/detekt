package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.SplitPattern
import java.nio.file.Path
import java.nio.file.PathMatcher

class PathFilters internal constructor(
    private val includes: Set<PathMatcher>?,
    private val excludes: Set<PathMatcher>?
) {

    companion object {
        fun of(includes: String?, excludes: String?): PathFilters? {
            if (includes == null && excludes == null) {
                return null
            }
            return PathFilters(parse(includes), parse(excludes))
        }

        private fun parse(value: String?): Set<PathMatcher>? =
            if (value == null) {
                null
            } else {
                SplitPattern(value, removeTrailingAsterisks = false)
                    .mapAll { pathMatcher(it) }.toSet()
            }
    }

    fun isIgnored(path: Path): Boolean {

        fun isIncluded() = includes?.any { it.matches(path) }
        fun isExcluded() = excludes?.any { it.matches(path) }

        return isIncluded()?.not() ?: isExcluded() ?: true
    }
}

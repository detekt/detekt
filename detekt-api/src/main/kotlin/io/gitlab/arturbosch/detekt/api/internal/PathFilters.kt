package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.commaSeparatedPattern
import java.nio.file.Path
import java.nio.file.PathMatcher

class PathFilters internal constructor(
    private val includes: Set<PathMatcher>?,
    private val excludes: Set<PathMatcher>?
) {

    fun isIgnored(path: Path): Boolean {

        fun isIncluded() = includes?.any { it.matches(path) }
        fun isExcluded() = excludes?.any { it.matches(path) }

        return isIncluded()?.not() ?: isExcluded() ?: true
    }

    companion object {
        fun of(includes: List<String>, excludes: List<String>): PathFilters? {
            if (includes.isEmpty() && excludes.isEmpty()) {
                return null
            }
            return PathFilters(parse(includes), parse(excludes))
        }

        private fun parse(value: List<String>): Set<PathMatcher>? =
            if (value.isEmpty()) {
                null
            } else {
                value
                    .map { pathMatcher(it) }
                    .toSet()
            }
    }
}

fun Config.createPathFilters(): PathFilters? {
    val includes = valueOrDefaultCommaSeparated(Config.INCLUDES_KEY, emptyList())
    val excludes = valueOrDefaultCommaSeparated(Config.EXCLUDES_KEY, emptyList())
    return PathFilters.of(includes, excludes)
}

fun Config.valueOrDefaultCommaSeparated(
    key: String,
    default: List<String>
): List<String> {
    fun fallBack() = valueOrDefault(key, default.joinToString(","))
        .trim()
        .commaSeparatedPattern(",", ";")
        .toList()

    return try {
        valueOrDefault(key, default)
    } catch (_: IllegalStateException) {
        fallBack()
    } catch (_: ClassCastException) {
        fallBack()
    }
}

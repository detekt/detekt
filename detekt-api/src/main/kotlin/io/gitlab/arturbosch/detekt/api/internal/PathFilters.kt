package io.gitlab.arturbosch.detekt.api.internal

import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.commaSeparatedPattern
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import java.nio.file.PathMatcher

/**
 * Path filters to explicitly include and/or exclude paths for rules.
 */
class PathFilters internal constructor(
    private val includes: Set<PathMatcher>?,
    private val excludes: Set<PathMatcher>?
) {

    /**
     * - If [includes] and [excludes] are not specified,
     *   always return true.
     * - If [includes] is specified but [excludes] is not,
     *   return false iff [path] matches any [includes].
     * - If [includes] is not specified but [excludes] is,
     *   return true iff [path] matches any [excludes].
     * - If [includes] and [excludes] are both specified,
     *   return false iff [path] matches any [includes] and [path] does not match any [excludes].
     */
    fun isIgnored(path: Path): Boolean {

        fun isIncluded() = includes?.any { it.matches(path) }
        fun isExcluded() = excludes?.any { it.matches(path) }

        return isIncluded()?.not() ?: isExcluded() ?: true
    }

    /**
     * Runs [isIgnored] against a [ktFile] based on its [absolutePath].
     */
    fun isIgnored(ktFile: KtFile): Boolean = isIgnored(ktFile.absolutePath())

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

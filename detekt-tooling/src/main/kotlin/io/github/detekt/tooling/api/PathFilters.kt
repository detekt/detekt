package io.github.detekt.tooling.api

import io.github.detekt.psi.absolutePath
import io.github.detekt.psi.basePath
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import java.nio.file.PathMatcher
import kotlin.io.path.Path
import kotlin.io.path.relativeTo

/**
 * Path filters to explicitly include and/or exclude paths for rules.
 */
class PathFilters internal constructor(
    private val includes: Set<PathMatcher>?,
    private val excludes: Set<PathMatcher>?
) {

    /**
     * - If [includes] and [excludes] are not specified,
     *   return false.
     * - If [includes] is specified but [excludes] is not,
     *   return false iff [path] matches any [includes].
     * - If [includes] is not specified but [excludes] is,
     *   return true iff [path] matches any [excludes].
     * - If [includes] and [excludes] are both specified,
     *   return false iff [path] matches any [includes] and [path] does not match any [excludes].
     */
    fun isIgnored(path: Path): Boolean {
        fun isIncluded() = includes?.any { it.matches(path) } ?: true
        fun isExcluded() = excludes?.any { it.matches(path) } ?: false

        return !(isIncluded() && !isExcluded())
    }

    /**
     * Runs [isIgnored] against a [KtFile] based on its relative path, or based on its [absolutePath] if [basePath] is
     * not set.
     */
    fun isIgnored(ktFile: KtFile): Boolean {
        ktFile.basePath?.let {
            return isIgnored(Path(".", ktFile.absolutePath().relativeTo(it).toString()))
        }
        return isIgnored(ktFile.absolutePath())
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

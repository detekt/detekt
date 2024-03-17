package io.gitlab.arturbosch.detekt.core.util

import io.github.detekt.psi.absolutePath
import io.github.detekt.psi.basePath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import org.jetbrains.kotlin.psi.KtFile
import kotlin.io.path.Path
import kotlin.io.path.relativeTo

internal fun Config.isActiveOrDefault(default: Boolean): Boolean = valueOrDefault(Config.ACTIVE_KEY, default)

internal fun Config.shouldAnalyzeFile(file: KtFile): Boolean {
    val filters = createPathFilters()
    return filters == null || !filters.isIgnored(file)
}

private fun Config.createPathFilters(): PathFilters? {
    val includes = valueOrDefault(Config.INCLUDES_KEY, emptyList<String>())
    val excludes = valueOrDefault(Config.EXCLUDES_KEY, emptyList<String>())
    return PathFilters.of(includes, excludes)
}

/**
 * Runs [isIgnored] against a [KtFile] based on its relative path, or based on its [absolutePath] if [basePath] is
 * not set.
 */
private fun PathFilters.isIgnored(ktFile: KtFile): Boolean {
    ktFile.basePath?.let {
        return isIgnored(Path(".", ktFile.absolutePath().relativeTo(it).toString()))
    }
    return isIgnored(ktFile.absolutePath())
}

internal fun String.indentCompat(indent: Int): String {
    val spaces = " ".repeat(indent)
    return this.lines().joinToString("\n") { spaces + it }
}

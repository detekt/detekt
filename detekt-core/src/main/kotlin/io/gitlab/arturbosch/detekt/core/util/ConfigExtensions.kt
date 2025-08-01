package io.gitlab.arturbosch.detekt.core.util

import dev.detekt.api.Config
import dev.detekt.psi.absolutePath
import dev.detekt.utils.PathFilters
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.relativeTo

internal fun Config.isActiveOrDefault(default: Boolean): Boolean = valueOrDefault(Config.ACTIVE_KEY, default)

internal fun Config.shouldAnalyzeFile(file: KtFile, basePath: Path): Boolean {
    val filters = createPathFilters()
    return filters == null || !filters.isIgnored(file, basePath)
}

private fun Config.createPathFilters(): PathFilters? {
    val includes = valueOrDefault(Config.INCLUDES_KEY, emptyList<String>())
    val excludes = valueOrDefault(Config.EXCLUDES_KEY, emptyList<String>())
    return PathFilters.of(includes, excludes)
}

/**
 * Runs [isIgnored] against a [KtFile] based on its relative path.
 */
private fun PathFilters.isIgnored(ktFile: KtFile, basePath: Path): Boolean =
    isIgnored(Path(".", ktFile.absolutePath().relativeTo(basePath).toString()))

internal fun String.indentCompat(indent: Int): String {
    val spaces = " ".repeat(indent)
    return this.lines().joinToString("\n") { spaces + it }
}

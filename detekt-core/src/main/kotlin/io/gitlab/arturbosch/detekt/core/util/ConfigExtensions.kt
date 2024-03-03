package io.gitlab.arturbosch.detekt.core.util

import io.github.detekt.tooling.api.PathFilters
import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtFile

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

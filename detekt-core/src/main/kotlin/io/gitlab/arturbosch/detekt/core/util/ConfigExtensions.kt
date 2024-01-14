package io.gitlab.arturbosch.detekt.core.util

import io.github.detekt.tooling.api.createPathFilters
import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtFile

internal fun Config.isActiveOrDefault(default: Boolean): Boolean = valueOrDefault(Config.ACTIVE_KEY, default)

internal fun Config.shouldAnalyzeFile(file: KtFile): Boolean {
    val filters = createPathFilters()
    return filters == null || !filters.isIgnored(file)
}

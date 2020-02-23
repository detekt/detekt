package io.gitlab.arturbosch.detekt.core.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.api.internal.absolutePath
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Paths

fun RuleSetProvider.isActive(config: Config): Boolean =
    config.subConfig(ruleSetId)
        .valueOrDefault("active", true)

fun RuleSetProvider.createRuleSet(config: Config): RuleSet =
    instance(config.subConfig(ruleSetId))

fun RuleSet.shouldAnalyzeFile(file: KtFile, config: Config): Boolean {
    fun filters(): PathFilters? {
        val subConfig = config.subConfig(id)
        val includes = subConfig.valueOrNull<String>(Config.INCLUDES_KEY)?.trim()
        val excludes = subConfig.valueOrNull<String>(Config.EXCLUDES_KEY)?.trim()
        return PathFilters.of(includes, excludes)
    }

    val filters = filters()
    if (filters != null) {
        val path = Paths.get(file.absolutePath())
        return !filters.isIgnored(path)
    }
    return true
}

fun RuleSet.visitFile(
    file: KtFile,
    bindingContext: BindingContext = BindingContext.EMPTY
): List<Finding> =
    rules.flatMap {
        it.visitFile(file, bindingContext)
        it.findings
    }

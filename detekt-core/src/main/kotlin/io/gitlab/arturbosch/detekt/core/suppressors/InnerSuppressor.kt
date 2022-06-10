@file:Suppress("WildcardImport", "NoWildcardImports")
package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.resolve.BindingContext

private fun buildSuppressors(rule: ConfigAware, bindingContext: BindingContext): List<Suppressor> {
    return listOfNotNull(
        annotationSuppressorFactory(rule, bindingContext),
        functionSuppressorFactory(rule, bindingContext),
    )
}

internal fun getSuppressors(rule: BaseRule, bindingContext: BindingContext): List<Suppressor> {
    return when (rule) {
        is MultiRule -> rule.rules.flatMap { innerRule ->
            buildSuppressors(innerRule, bindingContext).map { suppressor -> InnerSuppressor(innerRule, suppressor) }
        }
        is ConfigAware -> buildSuppressors(rule, bindingContext)
        else -> emptyList()
    }
}

private class InnerSuppressor(
    private val rule: Rule,
    private val suppressor: Suppressor
) : Suppressor {
    override fun shouldSuppress(finding: Finding): Boolean {
        return if (finding.id == rule.issue.id) {
            suppressor.shouldSuppress(finding)
        } else {
            false
        }
    }
}

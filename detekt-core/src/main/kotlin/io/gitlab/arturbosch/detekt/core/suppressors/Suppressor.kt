package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.resolve.BindingContext

fun interface Suppressor {
    /**
     * Given a Finding it decides if it should be suppressed (`true`) or not (`false`)
     */
    fun shouldSuppress(finding: Finding): Boolean
}

private fun buildSuppressors(rule: ConfigAware, bindingContext: BindingContext): List<Suppressor> {
    return listOfNotNull(
        annotationSuppressorFactory(rule, bindingContext),
        functionSuppressorFactory(rule, bindingContext),
    )
}

internal fun getSuppressors(rule: BaseRule, bindingContext: BindingContext): List<Suppressor> {
    @Suppress("DEPRECATION")
    return when (rule) {
        is io.gitlab.arturbosch.detekt.api.MultiRule -> rule.rules.flatMap { innerRule ->
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

package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule

/**
 * Given a Finding it decides if it should be suppressed (`true`) or not (`false`)
 */
typealias Suppressor = (Finding) -> Boolean

private fun buildSuppressors(rule: ConfigAware): List<Suppressor> {
    return listOfNotNull(
        annotationSuppressorFactory(rule),
    )
}

internal fun getSuppressors(rule: BaseRule): List<Suppressor> {
    return when (rule) {
        is MultiRule -> rule.rules.flatMap { innerRule ->
            buildSuppressors(innerRule).map { suppressor -> InnerSuppressor(innerRule, suppressor) }
        }
        is ConfigAware -> buildSuppressors(rule)
        else -> emptyList()
    }
}

private class InnerSuppressor(
    private val rule: Rule,
    private val suppressor: Suppressor
) : Suppressor {
    override fun invoke(finding: Finding): Boolean {
        return if (finding.id == rule.issue.id) {
            suppressor(finding)
        } else {
            false
        }
    }
}

package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.api.IssuesFound
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.reporting.filterAutoCorrectedIssues

internal fun RulesSpec.FailurePolicy.check(result: Detektion, config: Config) {
    when (this) {
        RulesSpec.FailurePolicy.NoneAllowed -> {
            if (result.computeIssueCount(config) > 0) {
                throw IssuesFound("Analysis failed with ${result.findings.size} issues.")
            }
        }

        else -> error("Unsupported failure policy '$this'.")
    }
}

private fun Detektion.computeIssueCount(config: Config): Int {
    val smells = filterAutoCorrectedIssues(config).flatMap { it.value }
    return smells.count()
}

package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.api.IssuesFound
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.core.reporting.filterAutoCorrectedIssues

internal fun RulesSpec.FailurePolicy.check(result: Detektion, config: Config) {
    when (this) {
        is RulesSpec.FailurePolicy.FailOnError -> {
            val issueCount = result.computeIssueCount(config)
            if (issueCount > 0) {
                throw IssuesFound("Analysis failed with $issueCount issues.")
            }
        }
    }
}

private fun Detektion.computeIssueCount(config: Config): Int {
    val smells = filterAutoCorrectedIssues(config)
        .flatMap { it.value }
        .filter { it.severity == Severity.ERROR }
    return smells.count()
}

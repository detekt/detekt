package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.api.IssuesFound
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.core.reporting.filterAutoCorrectedIssues

internal fun RulesSpec.FailurePolicy.check(result: Detektion, config: Config) {
    when (this) {
        is RulesSpec.FailurePolicy.FailOnSeverity -> result.checkForIssuesWithSeverity(config, minSeverity)
        RulesSpec.FailurePolicy.DefaultFailurePolicy -> result.checkForIssuesWithSeverity(config, Severity.ERROR)
        RulesSpec.FailurePolicy.NeverFail -> Unit
    }
}

private fun Detektion.checkForIssuesWithSeverity(config: Config, minSeverity: Severity) {
    val issueCount = computeIssueCount(config, minSeverity)
    if (issueCount > 0) {
        throw IssuesFound("Analysis failed with $issueCount issues.")
    }
}

private fun Detektion.computeIssueCount(config: Config, minSeverity: Severity): Int =
    filterAutoCorrectedIssues(config)
        .flatMap { it.value }
        .count { it.severity.isAtLeast(minSeverity) }

private fun Severity.isAtLeast(severity: Severity): Boolean = this.ordinal <= severity.ordinal

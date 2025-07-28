package io.gitlab.arturbosch.detekt.core.config

import dev.detekt.tooling.api.IssuesFound
import dev.detekt.tooling.api.spec.RulesSpec
import dev.detekt.api.Detektion
import dev.detekt.api.Severity
import dev.detekt.api.suppressed

internal fun RulesSpec.FailurePolicy.check(result: Detektion) {
    when (this) {
        RulesSpec.FailurePolicy.NeverFail -> Unit
        is RulesSpec.FailurePolicy.FailOnSeverity -> result.checkForIssuesWithSeverity(minSeverity)
    }
}

private fun Detektion.checkForIssuesWithSeverity(minSeverity: Severity) {
    val issueCount = computeIssueCount(minSeverity)
    if (issueCount > 0) {
        throw IssuesFound("Analysis failed with $issueCount issues.")
    }
}

private fun Detektion.computeIssueCount(minSeverity: Severity): Int =
    issues.count { it.severity.isAtLeast(minSeverity) && !it.suppressed }

private fun Severity.isAtLeast(severity: Severity): Boolean = this.ordinal <= severity.ordinal

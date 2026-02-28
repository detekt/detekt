package dev.detekt.core.config

import dev.detekt.api.Detektion
import dev.detekt.api.Severity
import dev.detekt.api.suppressed
import dev.detekt.tooling.api.spec.RulesSpec

internal fun RulesSpec.FailurePolicy.check(result: Detektion): FailurePolicyResult =
    when (this) {
        RulesSpec.FailurePolicy.NeverFail -> FailurePolicyResult.Ok
        is RulesSpec.FailurePolicy.FailOnSeverity -> result.checkForIssuesWithSeverity(minSeverity)
    }

sealed interface FailurePolicyResult {
    data object Ok : FailurePolicyResult
    data class Fail(val message: String) : FailurePolicyResult
}

private fun Detektion.checkForIssuesWithSeverity(minSeverity: Severity): FailurePolicyResult {
    val issueCount = computeIssueCount(minSeverity)
    return if (issueCount > 0) {
        FailurePolicyResult.Fail("Analysis failed with $issueCount issues.")
    } else {
        FailurePolicyResult.Ok
    }
}

private fun Detektion.computeIssueCount(minSeverity: Severity): Int =
    issues.count { it.severity.isAtLeast(minSeverity) && !it.suppressed }

private fun Severity.isAtLeast(severity: Severity): Boolean = this.ordinal <= severity.ordinal

package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.tooling.api.MaxIssuesReached
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.reporting.BUILD

internal class MaxIssueCheck(
    private val rulesSpec: RulesSpec,
    private val config: Config
) {
    private val policy: RulesSpec.MaxIssuePolicy = run {
        if (rulesSpec.maxIssuePolicy == RulesSpec.MaxIssuePolicy.NonSpecified) {
            val configuredMaxIssues = config.subConfig(BUILD)
                .valueOrNull<Int>(MAX_ISSUES)

            when (configuredMaxIssues) {
                null -> rulesSpec.maxIssuePolicy
                0 -> RulesSpec.MaxIssuePolicy.NoneAllowed
                in 1..Int.MAX_VALUE -> RulesSpec.MaxIssuePolicy.AllowAmount(configuredMaxIssues)
                else -> RulesSpec.MaxIssuePolicy.AllowAny
            }
        } else {
            rulesSpec.maxIssuePolicy
        }
    }

    private fun meetsPolicy(numberOfIssues: Int): Boolean = when (policy) {
        RulesSpec.MaxIssuePolicy.AllowAny -> true
        RulesSpec.MaxIssuePolicy.NoneAllowed, RulesSpec.MaxIssuePolicy.NonSpecified -> numberOfIssues == 0
        is RulesSpec.MaxIssuePolicy.AllowAmount -> numberOfIssues <= policy.amount
    }

    fun check(numberOfIssues: Int) {
        if (!meetsPolicy(numberOfIssues)) {
            throw MaxIssuesReached("Build failed with $numberOfIssues weighted issues.")
        }
    }
}

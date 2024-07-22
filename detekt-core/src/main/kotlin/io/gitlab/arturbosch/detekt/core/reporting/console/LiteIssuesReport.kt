package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.core.reporting.compact

/**
 * A lightweight versions of the console report, where each line contains location, messages and issue id only.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class LiteIssuesReport : AbstractIssuesReport() {

    override val id: String = "LiteIssuesReport"

    override fun render(issues: List<Issue>): String =
        buildString {
            issues.forEach { issue ->
                append("${issue.location.compact()}: ${issue.message} [${issue.ruleInstance.id}]")
                appendLine()
            }
        }
}

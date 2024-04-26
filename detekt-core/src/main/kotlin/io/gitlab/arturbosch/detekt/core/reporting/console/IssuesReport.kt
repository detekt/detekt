package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.core.reporting.printIssues

/**
 * Contains all rule violations in a list format grouped by ruleset.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class IssuesReport : AbstractIssuesReport() {

    override val id: String = "IssuesReport"

    override fun render(issues: List<Issue>): String {
        return printIssues(issues.groupBy { it.ruleInstance.ruleSetId }.mapKeys { (key, _) -> key.value })
    }
}

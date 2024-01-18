package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.core.reporting.printFindings

/**
 * Contains all rule violations in a list format grouped by ruleset.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class FindingsReport : AbstractFindingsReport() {

    override val id: String = "FindingsReport"

    override fun render(findings: List<Finding2>): String {
        return printFindings(findings.groupBy { it.ruleInfo.ruleSetId }.mapKeys { (key, _) -> key.value })
    }
}

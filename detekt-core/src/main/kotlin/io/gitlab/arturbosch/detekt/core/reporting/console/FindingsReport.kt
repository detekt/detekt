package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.core.reporting.printFindings

/**
 * Contains all rule violations in a list format grouped by ruleset.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class FindingsReport : AbstractFindingsReport() {

    override val id: String = "FindingsReport"

    override fun render(findings: Map<RuleSet.Id, List<Finding>>): String {
        return printFindings(findings.mapKeys { (key, _) -> key.value })
    }
}

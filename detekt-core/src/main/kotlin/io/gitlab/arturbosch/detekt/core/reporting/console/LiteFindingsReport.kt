package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.RuleSet

/**
 * A lightweight versions of the console report, where each line contains location, messages and issue id only.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class LiteFindingsReport : AbstractFindingsReport() {

    override val id: String = "LiteFindingsReport"

    override fun render(findings: Map<RuleSet.Id, List<Finding2>>): String {
        return buildString {
            findings.values.flatten().forEach { finding ->
                append("${finding.location.compact()}: ${finding.message} [${finding.rule.id}]")
                appendLine()
            }
        }
    }
}

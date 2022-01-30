package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

/**
 * A lightweight versions of the console report, where each line contains location, messages and issue id only.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class LiteFindingsReport : AbstractFindingsReport() {

    override fun render(findings: Map<RuleSetId, List<Finding>>): String {
        return buildString {
            findings.values.flatten().forEach { finding ->
                append("${finding.location.compact()}: ${finding.messageOrDescription()} [${finding.issue.id}]")
                appendLine()
            }
        }
    }
}

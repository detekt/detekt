package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.core.reporting.filterEmptyIssues

/**
 * Contains a clear read of the console report, where each line contains location, messages and issue id.
 * See: https://detekt.github.io/detekt/configurations.html#console-reports
 */
class ReadableFindingsReport : ConsoleReport() {

    private var config: Config by SingleAssign()

    override val priority: Int = 40

    override fun init(config: Config) {
        this.config = config
    }

    override fun render(detektion: Detektion): String? {
        val issues = detektion.filterEmptyIssues(config)
        if (issues.isEmpty()) {
            return null
        }
        return buildString {
            issues.values.flatten().forEach { finding ->
                append("${finding.location.compact()}: ${finding.messageOrDescription()} [${finding.issue.id}]")
                appendLine()
            }
        }
    }
}

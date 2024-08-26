package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.core.reporting.printIssues
import java.nio.file.Path

/**
 * Contains all rule violations in a list format grouped by ruleset.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class IssuesReport : AbstractIssuesReport() {

    override val id: String = "IssuesReport"

    private lateinit var basePath: Path

    override fun init(context: SetupContext) {
        super.init(context)
        basePath = context.basePath
    }

    override fun render(issues: List<Issue>): String =
        printIssues(issues.groupBy { it.ruleInstance.ruleSetId }.mapKeys { (key, _) -> key.value }, basePath)
}

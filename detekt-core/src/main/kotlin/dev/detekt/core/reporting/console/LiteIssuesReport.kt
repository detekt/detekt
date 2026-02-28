package dev.detekt.core.reporting.console

import dev.detekt.api.Issue
import dev.detekt.api.SetupContext
import dev.detekt.core.reporting.compact
import dev.detekt.core.reporting.prefix
import java.nio.file.Path

/**
 * A lightweight versions of the console report, where each line contains location, messages and issue id only.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class LiteIssuesReport : AbstractIssuesReport() {

    override val id: String = "LiteIssuesReport"

    private lateinit var basePath: Path

    override fun init(context: SetupContext) {
        super.init(context)
        basePath = context.basePath
    }

    override fun render(issues: List<Issue>): String =
        buildString {
            issues.forEach { issue ->
                append(issue.severity.prefix())
                append("${issue.location.compact(basePath)} ${issue.message} [${issue.ruleInstance.id}]")
                appendLine()
            }
        }
}

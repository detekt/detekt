package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.core.reporting.printIssues

/**
 * Contains all rule violations grouped by file location.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class FileBasedIssuesReport : AbstractIssuesReport() {

    override val id: String = "FileBasedIssuesReport"

    override fun render(issues: List<Issue>): String {
        val issuesPerFile = issues.groupBy { it.entity.location.filePath.absolutePath.toString() }
        return printIssues(issuesPerFile)
    }
}

package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.core.reporting.printIssues
import java.nio.file.Path
import kotlin.io.path.absolutePathString

/**
 * Contains all rule violations grouped by file location.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class FileBasedIssuesReport : AbstractIssuesReport() {

    override val id: String = "FileBasedIssuesReport"

    private lateinit var basePath: Path

    override fun init(context: SetupContext) {
        super.init(context)
        basePath = context.basePath
    }

    override fun render(issues: List<Issue>): String {
        val issuesPerFile = issues.groupBy { basePath.resolve(it.entity.location.path).absolutePathString() }
        return printIssues(issuesPerFile, basePath)
    }
}

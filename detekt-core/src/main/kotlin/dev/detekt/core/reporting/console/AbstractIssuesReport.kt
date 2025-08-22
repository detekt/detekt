package dev.detekt.core.reporting.console

import dev.detekt.api.Config
import dev.detekt.api.ConsoleReport
import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.SetupContext
import dev.detekt.api.suppressed

abstract class AbstractIssuesReport : ConsoleReport {

    private lateinit var config: Config

    override val priority: Int = 40

    override fun init(context: SetupContext) {
        this.config = context.config
    }

    override fun render(detektion: Detektion): String? {
        val issues = detektion.issues.filterNot { issue -> issue.suppressed }
        if (issues.isEmpty()) {
            return null
        }
        return render(issues)
    }

    abstract fun render(issues: List<Issue>): String
}

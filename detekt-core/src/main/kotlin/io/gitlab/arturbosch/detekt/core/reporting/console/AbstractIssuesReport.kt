package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.core.reporting.filterEmptyIssues

abstract class AbstractIssuesReport : ConsoleReport {

    private lateinit var config: Config

    override val priority: Int = 40

    override fun init(config: Config) {
        this.config = config
    }

    override fun render(detektion: Detektion): String? {
        val issues = detektion.filterEmptyIssues(config)
        if (issues.isEmpty()) {
            return null
        }
        return render(issues)
    }

    abstract fun render(issues: List<Issue>): String
}

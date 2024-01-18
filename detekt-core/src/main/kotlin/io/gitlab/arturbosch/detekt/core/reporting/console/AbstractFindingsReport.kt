package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.core.reporting.filterEmptyIssues

abstract class AbstractFindingsReport : ConsoleReport() {

    private lateinit var config: Config

    override val priority: Int = 40

    override fun init(config: Config) {
        this.config = config
    }

    override fun render(detektion: Detektion): String? {
        val findings = detektion.filterEmptyIssues(config)
        if (findings.isEmpty()) {
            return null
        }
        return render(findings)
    }

    abstract fun render(findings: List<Finding2>): String
}

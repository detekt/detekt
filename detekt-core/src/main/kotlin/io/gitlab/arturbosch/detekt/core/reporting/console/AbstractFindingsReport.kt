package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.core.reporting.filterEmptyIssues

abstract class AbstractFindingsReport : ConsoleReport() {

    private var config: Config by SingleAssign()

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

    abstract fun render(findings: Map<RuleSetId, List<Finding>>): String
}

package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.cli.filterEmptyIssues

class FileBasedFindingsReport : ConsoleReport() {

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

        val findingsPerFile = findings.values
            .flatten()
            .groupBy { it.entity.location.file }
        return printFindings(findingsPerFile)
    }
}

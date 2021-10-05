package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.core.reporting.printFindings

/**
 * Contains all rule violations grouped by file location.
 * See: https://detekt.github.io/detekt/configurations.html#console-reports
 */
class FileBasedFindingsReport : AbstractFindingsReport() {

    override fun render(findings: Map<RuleSetId, List<Finding>>): String {
        val findingsPerFile = findings.values
            .flatten()
            .groupBy { it.entity.location.filePath.absolutePath.toString() }
        return printFindings(findingsPerFile)
    }
}

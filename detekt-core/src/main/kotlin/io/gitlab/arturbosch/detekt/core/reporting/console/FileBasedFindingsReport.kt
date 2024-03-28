package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.core.reporting.printFindings

/**
 * Contains all rule violations grouped by file location.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class FileBasedFindingsReport : AbstractFindingsReport() {

    override val id: String = "FileBasedFindingsReport"

    override fun render(findings: List<Finding2>): String {
        val findingsPerFile = findings.groupBy { it.entity.location.filePath.absolutePath.toString() }
        return printFindings(findingsPerFile)
    }
}

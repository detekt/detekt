package io.github.detekt.report.txt

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport

/**
 * Contains rule violations in a plain text report similar to a log file.
 * See: https://detekt.github.io/detekt/configurations.html#output-reports
 */
class TxtOutputReport : OutputReport() {

    override val ending: String = "txt"

    override val name = "plain text report"

    override fun render(detektion: Detektion): String {
        val builder = StringBuilder()
        detektion.findings
            .flatMap { it.value }
            .forEach { builder.append(it.compactWithSignature()).append("\n") }
        return builder.toString()
    }
}

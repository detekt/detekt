package io.github.detekt.report.txt

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.internal.BuiltInOutputReport

/**
 * Contains rule violations in a plain text report similar to a log file.
 * See: https://detekt.dev/configurations.html#output-reports
 */
class TxtOutputReport : BuiltInOutputReport, OutputReport() {

    override val id: String = "TxtOutputReport"
    override val ending: String = "txt"

    override fun render(detektion: Detektion): String {
        return detektion.issues
            .ifEmpty { return "" }
            .joinToString("\n", postfix = "\n") { it.compactWithSignature() }
    }
}

private fun Issue.compactWithSignature(): String =
    "${ruleInstance.id} - ${entity.compact()} - Signature=${entity.signature}"

private fun Entity.compact(): String = "[$name] at ${location.path}:${location.source}"

package io.github.detekt.report.xml

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.Severity

/**
 * Contains rule violations in an XML format. The report follows the structure of a Checkstyle report.
 * See: https://detekt.github.io/detekt/configurations.html#output-reports
 */
class XmlOutputReport : OutputReport() {

    override val ending = "xml"

    override val name = "Checkstyle XML report"

    private val Finding.severityLabel: String
        get() = when (issue.severity) {
            Severity.CodeSmell,
            Severity.Style,
            Severity.Warning,
            Severity.Maintainability,
            Severity.Performance -> "warning"
            Severity.Defect -> "error"
            Severity.Minor -> "info"
            Severity.Security -> "fatal"
        }

    override fun render(detektion: Detektion): String {
        val smells = detektion.findings.flatMap { it.value }

        val lines = ArrayList<String>()
        lines += "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        lines += "<checkstyle version=\"4.3\">"

        smells.groupBy { it.location.file }.forEach { (fileName, findings) ->
            lines += "<file name=\"${fileName.toXmlString()}\">"
            findings.forEach {
                lines += arrayOf(
                        "\t<error line=\"${it.location.source.line.toXmlString()}\"",
                        "column=\"${it.location.source.column.toXmlString()}\"",
                        "severity=\"${it.severityLabel.toXmlString()}\"",
                        "message=\"${it.messageOrDescription().toXmlString()}\"",
                        "source=\"${"detekt.${it.id.toXmlString()}"}\" />"
                ).joinToString(separator = " ")
            }
            lines += "</file>"
        }

        lines += "</checkstyle>"
        return lines.joinToString(separator = "\n")
    }

    private fun Any.toXmlString() = XmlEscape.escapeXml(toString().trim())
}

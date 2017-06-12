package io.gitlab.arturbosch.detekt.cli.out.format

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Issue
import java.nio.file.Path

/**
 * Generates an XML report following the structure of a Checkstyle report.
 */
class XmlOutputFormat(report: Path) : OutputFormat(report) {

    private sealed class MessageType(val label: String) {
        class Warning : MessageType("warning")
        class Info : MessageType("info")
        class Fatal : MessageType("fatal")
        class Error : MessageType("error")
    }

    override fun render(smells: List<Finding>): String {
        val lines = ArrayList<String>()
        lines += "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        lines += "<checkstyle version=\"4.3\">"

        smells.groupBy { it.location.file }.forEach { fileName, findings ->
            lines += "<file name=\"${fileName.toXmlString()}\">"
            findings.forEach {
                lines += arrayOf(
                        "\t<error line=\"${it.location.source.line.toXmlString()}\"",
                        "column=\"${it.location.source.column.toXmlString()}\"",
                        "severity=\"${it.messageType.label.toXmlString()}\"",
                        "message=\"${(it.message + "(${it.issue.id})").toXmlString()}\"",
                        "source=\"${"detekt.${it.issue.id.toXmlString()}"}\" />"
                ).joinToString(separator = " ")
            }
            lines += "</file>"
        }

        lines += "</checkstyle>"
        return lines.joinToString(separator = "\n")
    }

    private val Finding.messageType: MessageType
        get() = when (issue.severity) {
            Issue.Severity.CodeSmell -> MessageType.Warning()
            Issue.Severity.Style -> MessageType.Warning()
            Issue.Severity.Warning -> MessageType.Warning()
            Issue.Severity.Maintainability -> MessageType.Warning()
            Issue.Severity.Defect -> MessageType.Error()
            Issue.Severity.Minor -> MessageType.Info()
            Issue.Severity.Security -> MessageType.Fatal()
        }

    private fun Any.toXmlString() = XmlEscape.escapeXml(toString().trim())
}

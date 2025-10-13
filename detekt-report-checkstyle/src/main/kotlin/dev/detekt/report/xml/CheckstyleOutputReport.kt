package dev.detekt.report.xml

import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.OutputReport
import dev.detekt.api.suppressed
import java.util.Locale
import kotlin.io.path.invariantSeparatorsPathString

/**
 * Contains rule violations in an XML format. The report follows the structure of a Checkstyle report.
 */
class CheckstyleOutputReport : OutputReport {
    override val id: String = "checkstyle"

    private val Issue.severityLabel: String
        get() = severity.name.lowercase(Locale.US)

    override fun render(detektion: Detektion): String {
        val lines = ArrayList<String>()
        lines += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        lines += "<checkstyle version=\"4.3\">"

        detektion.issues
            .filterNot { it.suppressed }
            .groupBy { it.location.path }
            .forEach { (filePath, issues) ->
                lines += "<file name=\"${filePath.invariantSeparatorsPathString.toXmlString()}\">"
                issues.forEach {
                    lines += arrayOf(
                        "\t<error line=\"${it.location.source.line.toXmlString()}\"",
                        "column=\"${it.location.source.column.toXmlString()}\"",
                        "severity=\"${it.severityLabel.toXmlString()}\"",
                        "message=\"${it.message.toXmlString()}\"",
                        "source=\"${"detekt.${it.ruleInstance.id.toXmlString()}"}\" />"
                    ).joinToString(separator = " ")
                }
                lines += "</file>"
            }

        lines += "</checkstyle>"
        return lines.joinToString(separator = "\n")
    }

    private fun Any.toXmlString() = XmlEscape.escapeXml(toString().trim())
}

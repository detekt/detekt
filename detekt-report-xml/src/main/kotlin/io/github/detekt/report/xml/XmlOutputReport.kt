package io.github.detekt.report.xml

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.getOrNull
import io.gitlab.arturbosch.detekt.api.internal.BuiltInOutputReport
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.absolute
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.relativeTo

/**
 * Contains rule violations in an XML format. The report follows the structure of a Checkstyle report.
 * See: https://detekt.dev/configurations.html#output-reports
 */
class XmlOutputReport : BuiltInOutputReport, OutputReport() {

    override val id: String = "XmlOutputReport"
    override val ending = "xml"

    private val Issue.severityLabel: String
        get() = severity.name.lowercase(Locale.US)

    var basePath: Path? = null

    override fun init(context: SetupContext) {
        basePath = context.getOrNull<Path>(DETEKT_OUTPUT_REPORT_BASE_PATH_KEY)?.absolute()
    }

    override fun render(detektion: Detektion): String {
        val lines = ArrayList<String>()
        lines += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        lines += "<checkstyle version=\"4.3\">"

        detektion.issues
            .groupBy {
                basePath?.let { path -> it.location.filePath.absolutePath.relativeTo(path) }
                    ?: it.location.filePath.absolutePath
            }
            .forEach { (filePath, issues) ->
                lines += "<file name=\"${filePath.invariantSeparatorsPathString.toXmlString()}\">"
                issues.forEach {
                    lines += arrayOf(
                        "\t<error line=\"${it.location.source.line.toXmlString()}\"",
                        "column=\"${it.location.source.column.toXmlString()}\"",
                        "severity=\"${it.severityLabel.toXmlString()}\"",
                        "message=\"${it.message.toXmlString()}\"",
                        "source=\"${"detekt.${it.ruleInfo.id.value.toXmlString()}"}\" />"
                    ).joinToString(separator = " ")
                }
                lines += "</file>"
            }

        lines += "</checkstyle>"
        return lines.joinToString(separator = "\n")
    }

    private fun Any.toXmlString() = XmlEscape.escapeXml(toString().trim())
}
